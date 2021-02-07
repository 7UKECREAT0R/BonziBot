package com.lukecreator.BonziBot.CommandAPI;

import com.lukecreator.BonziBot.Data.PremiumItem;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.Permission;

public abstract class Command {

	public static int LAST_ID = 0;
	
	public int id; // Always unique, but not guaranteed to be the same.
	public int subCategory = 0; // For categorizing commands in the sub-help menus.
	
	public String unicodeIcon = "❓"; // The icon that shows in the help menu.
	public String name = "literally nothing"; // The name of the command. Used in execution. (b:name)
	public CommandArgCollection args = null; // The arguments this command takes.
	public String description = "does nothing. this command hasn't been programmed yet."; // Description for the help menu.
	public CommandCategory category = CommandCategory._HIDDEN; // The help category the command shows in.
	public boolean worksInDms = true; // Does this command work in private messages?
	public boolean moderatorOnly = false; // Does this command only work for moderators?
	public boolean adminOnly = false; // Does this command only work for the BIG BOYS?
	
	public void resetCooldown(CommandExecutionInfo e) {
		e.bonzi.cooldowns.resetCooldown(this, e.executor.getIdLong());
	}
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
	
	public boolean isPremiumItem = false; // is premium item on the shop
	public PremiumItem premiumItem = null; // the premium item if so
	protected void setPremiumItem(PremiumItem item) {
		this.isPremiumItem = true;
		this.premiumItem = item;
	}
	
	public Permission[] neededPermissions = new Permission[] { Permission.UNKNOWN };
	
	public void executeCommand(CommandExecutionInfo e) {}
}