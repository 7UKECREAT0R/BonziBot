package com.lukecreator.BonziBot.CommandAPI;

import net.dv8tion.jda.api.Permission;

public abstract class Command {

	public static int LAST_ID = 0;
	
	public int id; // Always unique, but not guaranteed to be the same.
	
	public String name = "NULL COMMAND";
	public String usage = "nullcommand <nullarg>";
	public String description = "Does nothing. This command hasn't been programmed yet.";
	public CommandCategory category = CommandCategory._TOPLEVEL;
	public boolean worksInDms = true; // TODO
	public boolean moderatorOnly = false; // TODO
	public boolean adminOnly = false;
	
	protected void setCooldown(long ms) {
		hasCooldown = true;
		cooldownMs = ms;
	}
	public boolean hasCooldown;
	public long cooldownMs;
	
	public boolean usesArgs = false;
	public int goalArgs = 0;
	public ArgsComparison argsCheck = ArgsComparison.EQUAL;
	public Permission[] neededPermissions = new Permission[] { Permission.UNKNOWN };
	
	public void executeCommand(CommandExecutionInfo e) {}
}