package com.lukecreator.BonziBot.Commands;

import net.dv8tion.jda.api.Permission;

public abstract class ACommand {

	public String name = "NULL COMMAND";
	public String usage = "nullcommand <nullarg>";
	public String description = "Does nothing. This command hasn't been programmed yet.";
	public CommandCategory category = CommandCategory._TOPLEVEL;
	
	public boolean usesArgs = false;
	public int goalArgs = 0;
	public ArgsComparison argsCheck = ArgsComparison.EQUAL;
	public Permission neededPermission = Permission.UNKNOWN;
	
	public void executeCommand(CommandExecutionInfo e) {
		
	}
}
