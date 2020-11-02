package com.lukecreator.BonziBot.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Constants;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

/*
 * Manages the loading of commands, argument
 * parsing, and 
 */
public class CommandSystem {
	
	List<ACommand> commands;
	
	public CommandSystem() {
		commands = new ArrayList<ACommand>();
		
		ServiceLoader<ACommand> classLoader =
			ServiceLoader.load(ACommand.class);
		for(ACommand implClass: classLoader) {
			commands.add(implClass);
		}
	}
	
	/*
	 * Parse the input and direct it to a command.
	 */
	public void onInput(CommandExecutionInfo info) {
		
		String text = info.fullText;
		if(BonziUtils.isWhitespace(text)) return;
		
		String[] parts = text.split
			(Constants.WHITESPACE_REGEX);
		if(parts.length == 0) return;
		
		String commandName = parts[0];
		
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
	
	void directCommand(CommandExecutionInfo info) {
		for(ACommand cmd: commands) {
			String cmdName = cmd.name;
			if(!info.commandName.equalsIgnoreCase(cmdName))
				continue;
			if(!checkQualifications(cmd, info))
				return;
			
			// Should be good to execute.
			cmd.executeCommand(info);
		}
		return;
	}
	boolean checkQualifications(ACommand cmd, CommandExecutionInfo info) {
		
		// Check arguments.
		if(cmd.usesArgs) {
			ArgsComparison ac = cmd.argsCheck;
			int ga = cmd.goalArgs;
			int al = info.args.length;
			
			boolean incorrect = 
				(ac == ArgsComparison.EQUAL && ga != al) |
				(ac == ArgsComparison.ANY_HIGHER && ga < al) |
				(ac == ArgsComparison.ANY_LOWER && ga > al);
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
		
		return true;
	}
}
