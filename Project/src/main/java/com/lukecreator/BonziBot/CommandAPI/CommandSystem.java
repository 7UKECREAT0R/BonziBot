package com.lukecreator.BonziBot.CommandAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.InternalLogger;
import com.lukecreator.BonziBot.Managers.AdminManager;
import com.lukecreator.BonziBot.Managers.CooldownManager;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
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
		directCommand(info.setCommandData
			(commandName, finalArgs));
	}
	public List<Command> getRegisteredCommands() {
		return commands;
	}
	
	void directCommand(CommandExecutionInfo info) {
		for(Command cmd: commands) {
			String cmdName = cmd.name;
			if(!info.commandName.equalsIgnoreCase(cmdName))
				continue;
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
		AdminManager am = bot.admins;
		if(cmd.adminOnly && !am.getIsAdmin(ex)) {
			BonziUtils.sendAdminOnly(cmd, info);
			return false;
		}
		
		// Check arguments.
		if(cmd.usesArgs) {
			ArgsComparison ac = cmd.argsCheck;
			int ga = cmd.goalArgs;
			int al = info.args.length;
			
			boolean incorrect = 
				(ac == ArgsComparison.EQUAL && ga != al) ||
				(ac == ArgsComparison.ANY_HIGHER && al < ga) ||
				(ac == ArgsComparison.ANY_LOWER && al > ga);
			if(incorrect) {
				BonziUtils.sendUsage(cmd, info);
				return false;
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
		
		// Check cooldown.
		CooldownManager cdManager = info.bonzi.cooldowns;
		long userId = info.executor.getIdLong();
		if(cmd.hasCooldown && cmd.cooldownMs > 0) {
			if(cdManager.userOnCooldown(cmd, userId)) {
				BonziUtils.sendOnCooldown(cmd, info, cdManager);
				return false;
			}
		}
		return true;
	}
}
