package com.lukecreator.BonziBot.Commands;

public abstract class ACommand {

	public String name = "NULL COMMAND";
	public String usage = "nullcommand <nullarg>";
	public String description = "Does nothing. This command hasn't been programmed yet.";
	public CommandCategory category = CommandCategory._TOPLEVEL;
	
	// Arguments
	public boolean usesArgs = false;
	public int goalArgs = 0;
	public ArgsComparison argsCheck
		= ArgsComparison.EQUAL;
}
