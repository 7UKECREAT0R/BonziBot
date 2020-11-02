package com.lukecreator.BonziBot.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Constants;

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
			if(info.commandName.equalsIgnoreCase(cmdName)) {
				
			}
		}
		return;
	}
}
