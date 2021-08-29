package com.lukecreator.BonziBot.CommandAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.InternalLogger;
import com.lukecreator.BonziBot.Data.Modifier;
import com.lukecreator.BonziBot.Data.PremiumItem;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Managers.CooldownManager;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;
import com.lukecreator.BonziBot.Managers.SpecialPeopleManager;
import com.lukecreator.BonziBot.Managers.UserAccountManager;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

/**
 * Manages the loading of commands, argument parsing, and execution.
 */
public class CommandSystem {
	
	List<Command> commands;
	
	public CommandSystem(Reflections refs) {
		commands = new ArrayList<Command>();
		
		try {
			Set<Class<? extends Command>> classes = refs.getSubTypesOf(Command.class);
			for(Class<? extends Command> c: classes) {
				Command inst = c.newInstance();
				inst.id = inst.name.hashCode();
				InternalLogger.print("Register " + inst.getFilteredCommandName() + ":" + inst.id);
				commands.add(inst);
			}
		} catch (InstantiationException | IllegalAccessException e) {
			InternalLogger.printError(e);
		}
		
		InternalLogger.print("Registered " + commands.size() + " commands.");
	}
	
	/**
	 * Parse the input and direct it to a command.
	 * Returns if a command was run successfully.
	 */
	public boolean onInput(CommandExecutionInfo info) {
		
		String text = info.fullText;
		if(BonziUtils.isWhitespace(text)) return false;
		
		String prefix = BonziUtils.getPrefixOrDefault(info);
		
		String[] parts = text.split
			(Constants.WHITESPACE_REGEX);
		if(parts.length == 0) return false;
		
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
			return false;
		commandName = commandName
			.substring(prefix.length());
		
		// Not counting the first word.
		int argsLength = parts.length - 1;
		String[] finalArgs = new String[argsLength];
		int written = 0;
		for(int i = 1; i < parts.length; i++) {
			String part = parts[i];
			
			// Combine all arguments surrounded with
			//   parenthesis as a whole argument.
			if(part.startsWith("\"")) {
				if(part.endsWith("\"")) {
					part = part.substring(1, part.length() - 1);
				} else {
					List<String> combine = new
						ArrayList<String>();
					combine.add(part);
					while(++i < parts.length) {
						String concat = parts[i];
						combine.add(concat);
						if(concat.endsWith("\""))
							break;
					}
					part = String.join(" ", combine).substring(1);
					part = part.substring(0, part.length() - 1);
				}
			}
			
			finalArgs[written++] = part;
		}
		String[] resizedArgs = new String[written];
		System.arraycopy(finalArgs, 0, resizedArgs, 0, written);
		
		// Send it to the right command.
		return directCommand(info, commandName, resizedArgs);
	}
	/**
	 * Parse slash-command input and redirect it to the right command.
	 * @param event
	 * @return
	 */
	public boolean onInput(SlashCommandEvent event, BonziBot bb) {
		String name = event.getName();
		List<OptionMapping> _args = event.getOptions();
		OptionMapping[] args = new OptionMapping[_args.size()];
		_args.toArray(args);
		CommandExecutionInfo info = new CommandExecutionInfo(event).setBonziBot(bb);
		
		if(info.isGuildMessage) {
			TextChannel tc = info.tChannel;
			Modifier[] mods = BonziUtils.getChannelModifiers(tc);
			info.setModifiers(mods);
		} else {
			info.setModifiers();
		}
		
		for(Command cmd: commands) {
			
			if(!cmd.getSlashCommandName().equals(name))
				continue;
			
			if(info.settings != null) {
				if(!info.settings.botCommandsEnabled & !cmd.forcedCommand) {
					// Check for bot commands modifier.
					boolean good = false;
					for(Modifier mod: info.modifiers) {
						if(mod == Modifier.BOT_COMMANDS) {
							good = true;
							break;
						}
					}
					if(!good)
						return false;
				}
				List<Integer> disabled = info.settings.disabledCommands;
				if(disabled != null) {
					for(int test: disabled) {
						if(test == cmd.id) {
							BonziUtils.sendCommandDisabled(cmd, info);
							return false;
						}
					}
				}
			}
			
			// Validate any args with formatValidate true.
			if(cmd.args != null && cmd.args.args != null) {
				for(int i = 0; i < cmd.args.args.length; i++) {
					CommandArg arg = cmd.args.args[i];
					if(arg.optional && i >= args.length)
						break;
					if(!arg.type.formatValidate)
						continue;
					OptionMapping mapping = null;
					for(OptionMapping test: args)
						if(test.getName().replace('-', ' ').equalsIgnoreCase(arg.argName))
							mapping = test;
					if(mapping == null)
						continue;
					String input = mapping.getAsString();
					if(!arg.isWordParsable(input, event.getGuild())) {
						BonziUtils.sendUsage(cmd, info, false, arg);
						return false;
					}
				}
			}
			
			CommandParsedArgs cpa = null;
			if(cmd.args != null) {
				Guild inputGuild = info.isGuildMessage ? info.guild : null;
				cpa = cmd.args.parse(args, info.bot, info.executor, inputGuild);
			}
			
			info.setCommandData(cmd.getFilteredCommandName(), new String[args.length], cpa);
			
			if(!checkQualifications(cmd, info))
				return false;
			
			// Set cooldown.
			if(cmd.hasCooldown) {
				CooldownManager cm = info.bonzi.cooldowns;
				long userId = info.executor.getIdLong();
				cm.applyCooldown(cmd, userId);
			}
			
			// Should be good to execute.
			cmd.executeCommand(info);
			return true;
		}
		return false;
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
	public Command getCommandById(int id) {
		for(Command cmd: commands) {
			if(cmd.id == id)
				return cmd;
		}
		return null;
	}
	
	boolean directCommand(CommandExecutionInfo info, String commandName, String[] inputArgs) {
		for(Command cmd: commands) {
			String cmdName = cmd.getFilteredCommandName();
			
			if(!commandName.equalsIgnoreCase(cmdName))
				continue;
			
			// Check if command is disabled.
			if(info.settings != null) {
				List<Integer> disabled = info.settings.disabledCommands;
				if(disabled != null) {
					for(Integer test: disabled) {
						if(test.intValue() == cmd.id) {
							BonziUtils.sendCommandDisabled(cmd, info);
							return false;
						}
					}
				}
			}
			
			CommandParsedArgs cpa = null;
			if(cmd.args != null) {
				Guild inputGuild = info.isGuildMessage ? info.guild : null;
				cpa = cmd.args.parse(inputArgs, info.bot, info.executor, inputGuild);
			}
			info.setCommandData(commandName, inputArgs, cpa);
			
			if(!checkQualifications(cmd, info))
				return false;
			
			// Set cooldown.
			if(cmd.hasCooldown) {
				CooldownManager cm = info.bonzi.cooldowns;
				long userId = info.executor.getIdLong();
				cm.applyCooldown(cmd, userId);
			}
			
			// Should be good to execute.
			cmd.executeCommand(info);
			return true;
		}
		return false;
	}
	@SuppressWarnings("deprecation")
	boolean checkQualifications(Command cmd, CommandExecutionInfo info) {
		
		// If this is a shop item, check if the user owns it.
		if(cmd.isPremiumItem) {
			PremiumItem item = cmd.premiumItem;
			User user = info.executor;
			UserAccountManager uam = info.bonzi.accounts;
			UserAccount account = uam.getUserAccount(user);
			if(!account.hasItem(item)) {
				String prefix = BonziUtils.getPrefixOrDefault(info);
				BonziUtils.sendNotPurchased(cmd, info, prefix);
				return false;
			}
		}
		
		// Is bonzi awaiting confirmation via eventwaiter?
		EventWaiterManager ewm = info.bonzi.eventWaiter;
		if(ewm.isWaitingForAction(info.executor) ||
		 ewm.isWaitingForReaction(info.executor)) {
			BonziUtils.sendAwaitingConfirmation(info);
			return false;
		}
		
		// Check administrator.
		User ex = info.executor;
		BonziBot bot = info.bonzi;
		SpecialPeopleManager am = bot.special;
		boolean admin = am.getIsAdmin(ex);
		if(cmd.adminOnly && !admin) {
			BonziUtils.sendAdminOnly(cmd, info);
			return false;
		}
		
		// Check arguments.
		if(cmd.args != null && !info.isSlashCommand) {
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
				Guild g = info.isGuildMessage ? info.guild : null;
				boolean able = arg.isWordParsable(word, g);
				if(!able) {
					BonziUtils.sendUsage(cmd, info, false, arg);
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
		
		// Check user permissions.
		if(info.isGuildMessage && cmd.userRequiredPermissions != null) {
			if(!(info.bonzi.adminBypassing && admin)) {
				Member executor = info.member;
				boolean good = true;
				for(Permission p: cmd.userRequiredPermissions) {
					if(!executor.hasPermission(p)) {
						good = false;
						break;
					}
				}
				
				if(!good) {
					BonziUtils.sendUserNeedsPerms(cmd, info);
					return false;
				}
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