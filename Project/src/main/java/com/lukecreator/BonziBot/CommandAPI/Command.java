package com.lukecreator.BonziBot.CommandAPI;

import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.Permission;

public abstract class Command {

	public static int LAST_ID = 0;
	
	public int id; // Always unique, but not guaranteed to be the same.
	public int subCategory = 0; // For categorizing commands in the sub-help menus.
	
	public String unicodeIcon = "❓";
	public String name = "NULLCOMMAND";
	public CommandArgCollection args = null;
	public String description = "Does nothing. This command hasn't been programmed yet.";
	public CommandCategory category = CommandCategory._TOPLEVEL;
	public boolean worksInDms = true;
	public boolean moderatorOnly = false;
	public boolean adminOnly = false;
	
	/*
	 * Strip special characters for
	 *  comparison, such as spaces.
	 */
	public String getFilteredCommandName() {
		return name.replaceAll(Constants.WHITESPACE_REGEX, "").toLowerCase();
	}
	protected void setCooldown(long ms) {
		hasCooldown = true;
		cooldownMs = ms;
	}
	protected void setIcon(String unicode) {
		unicodeIcon = unicode;
	}
	public boolean hasCooldown;
	public long cooldownMs;
	
	public Permission[] neededPermissions = new Permission[] { Permission.UNKNOWN };
	
	public void executeCommand(CommandExecutionInfo e) {}
}