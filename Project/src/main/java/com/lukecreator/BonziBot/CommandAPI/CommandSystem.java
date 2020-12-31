package com.lukecreator.BonziBot.CommandAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.InternalLogger;
import com.lukecreator.BonziBot.Data.Modifier;
import com.lukecreator.BonziBot.Managers.CooldownManager;
import com.lukecreator.BonziBot.Managers.ModeratorManager;
import com.lukecreator.BonziBot.Managers.SpecialPeopleManager;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

/*
 * Manages the loading of commands, argument
 * parsing, and execution.
 */
public class CommandSystem {
	
	List<Command> commands;
	
	public CommandSystem() {
		commands = new ArrayList<Command>();
		
		try {
			Reflections refs = new Reflections("com.lukecreator.BonziBot");
			Set<Class<? extends Command>> classes = refs.getSubTypesOf(Command.class);
			for(Class<? extends Command> c: classes) {
				Command inst = c.newInstance();
				inst.id = Command.LAST_ID++;
				commands.add(inst);
			}
		} catch (InstantiationException | IllegalAccessException e) {
			InternalLogger.printError(e);
		}
		
		InternalLogger.print("Registered " + commands.size() + " commands.");
	}
	
	/*
	 * Parse the input and direct it to a command.
	 */
	public void onInput(CommandExecutionInfo info) {
		
		String text = info.fullText;
		if(BonziUtils.isWhitespace(text)) return;
		
		String prefix = BonziUtils.getPrefixOrDefault(info);
		
		String[] parts = text.split
			(Constants.WHITESPACE_REGEX);
		if(parts.length == 0) return;
		
		// Assign modifiers for the current channel.
		if(info.isGuildMessage) {
			TextChannel tc = info.tChannel;
			Modifier[] mods = BonziUtils.getChannelModifiers(tc);
			info.setModifiers(mods);
		} else {
			info.setModifiers(); // Empty array.
		}
		
		String commandName = parts[0];
		String puc = prefix.toUpperCase();
		String cuc = commandName.toUpperCase();
		if(!cuc.startsWith(puc))
			return;
		commandName = commandName
			.substring(prefix.length());
		
		// Not counting the first word.
		int argsLength = parts.length - 1;
		String[] finalArgs = new String[argsLength];
		for(int i = 1; i < parts.length; i++) {
			finalArgs[i - 1] = parts[i];
		}
		
		// Send it to the right command.
		directCommand(info, commandName, finalArgs);
	}
	public List<Command> getRegisteredCommands() {
		return commands;
	}
	public List<Command> getCommandsWithCategory(CommandCategory cat) {
		ArrayList<Command> list = new ArrayList<Command>();
		for(Command cmd: commands) {
			if(cmd.category == cat)
				list.add(cmd);
		}
		return list;
	}
	public Command getCommandByName(String name) {
		Command find = null;
		for(Command cmd: commands) {
			if(cmd.name.equalsIgnoreCase(name))
				find = cmd;
			if(find == null && cmd
					.getFilteredCommandName()
					.equalsIgnoreCase(name))
				find = cmd;
			if(find != null)
				break;
		}
		return find;
	}
	
	void directCommand(CommandExecutionInfo info, String commandName, String[] inputArgs) {
		for(Command cmd: commands) {
			String cmdName = cmd.getFilteredCommandName();
			if(!commandName.equalsIgnoreCase(cmdName))
				continue;
			
			CommandParsedArgs cpa = null;
			if(cmd.args != null)
				cpa = cmd.args.parse(inputArgs, info.bot, info.executor);
			info.setCommandData(commandName, inputArgs, cpa);
			
			if(!checkQualifications(cmd, info))
				return;
			
			// Set cooldown.
			if(cmd.hasCooldown) {
				CooldownManager cm = info.bonzi.cooldowns;
				long userId = info.executor.getIdLong();
				cm.applyCooldown(cmd, userId);
			}
			
			// Should be good to execute.
			cmd.executeCommand(info);
			
			// End
			break;
		}
		return;
	}
	boolean checkQualifications(Command cmd, CommandExecutionInfo info) {
		
		// Check administrator.
		User ex = info.executor;
		BonziBot bot = info.bonzi;
		SpecialPeopleManager am = bot.special;
		if(cmd.adminOnly && !am.getIsAdmin(ex)) {
			BonziUtils.sendAdminOnly(cmd, info);
			return false;
		}
		
		// Check arguments.
		if(cmd.args != null) {
			// Check if illegal optional argument exists.
			cmd.args.testValidity();
			
			if(info.args.underpopulated) {
				BonziUtils.sendUsage(cmd, info, true, null);
				return false;
			}
			for(int i = 0; i < cmd.args.args.length; i++) {
				CommandArg arg = cmd.args.args[i];
				if(arg.optional && i >= info.inputArgs.length)
					break;
				String word = info.inputArgs[i];
				boolean able = arg.isWordParsable(word);
				if(!able) {
					BonziUtils.sendUsage(cmd,
						info, false, arg);
					return false;
				}
			}
		}
		
		// Check permissions.
		boolean hasPerms = cmd.neededPermissions[0] != Permission.UNKNOWN;
		if(hasPerms && info.isGuildMessage) {
			Guild guild = info.guild;
			Member self = guild.getSelfMember();
			if(!self.hasPermission(cmd.neededPermissions)) {
				BonziUtils.sendNeededPerms(cmd, info);
				return false;
			}
		}
		
		// Check moderator.
		if(cmd.moderatorOnly) {
			if(info.isDirectMessage) {
				BonziUtils.sendDoesntWorkDms(cmd, info);
				return false;
			}
			Guild guild = info.guild;
			String prefix = bot.prefixes.getPrefix(guild);
			ModeratorManager mods = bot.moderators;
			if(!mods.canCreateModRole(guild)) {
				BonziUtils.sendNeededPermsForModRole(cmd, info, prefix);
				return false;
			}
			Role modRole = mods.getModRole(guild);
			Member member = info.member;
			List<Role> roles = member.getRoles();
			boolean isMod = false;
			
			if(member.hasPermission(Permission.ADMINISTRATOR) || member.isOwner())
				isMod = true;
			
			if(!isMod) {
				for(Role r: roles) {
					if(r.getIdLong() == modRole.getIdLong()) {
						isMod = true;
						break;
					}
				}
			}
			
			if(!isMod) {
				BonziUtils.sendModOnly(cmd, info, prefix);
				return true;
			}
		}
		
		// Check cooldown.
		CooldownManager cdManager = info.bonzi.cooldowns;
		long userId = info.executor.getIdLong();
		if(cmd.hasCooldown && cmd.cooldownMs > 0) {
			if(cdManager.userOnCooldown(cmd, userId)) {
				BonziUtils.sendOnCooldown(cmd, info, cdManager);
				return false;
			}
		}
		
		// Check if dms aren't allowed.
		if(!cmd.worksInDms && info.isDirectMessage) {
			BonziUtils.sendDoesntWorkDms(cmd, info);
			return false;
		}
		return true;
	}
}