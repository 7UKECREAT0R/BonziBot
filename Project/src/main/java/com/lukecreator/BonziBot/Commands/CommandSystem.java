package com.lukecreator.BonziBot.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

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
	
	public void onInput(CommandExecutionInfo info) {
		
	}
}
