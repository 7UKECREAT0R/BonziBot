package com.lukecreator.BonziBot.CommandAPI;

import com.lukecreator.BonziBot.Data.PremiumItem;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public abstract class Command {
	
	public int id; // Always unique and should persist.
	public int subCategory = 0; // For categorizing commands in the sub-help menus.
	
	public String unicodeIcon = "❓"; 							// The icon that shows in the help menu.
	public String name = "literally nothing"; 					// The name of the command. Used in execution. (b:name)
	public CommandArgCollection args = null; 					// The arguments this command takes.
	public String description = "does nothing."; 				// Description for the help menu.
	public CommandCategory category = CommandCategory._HIDDEN; 	// The help category the command shows in.
	public boolean worksInDms = true; 							// Does this command work in private messages?
	public Permission[] userRequiredPermissions = null; 		// The permissions the user needs to actually run the command.
	public boolean adminOnly = false; 							// Does this command only work for the BIG BOYS?
	public boolean forcedCommand = false;						// You cannot disable this command and it will always work.
	
	public void resetCooldown(CommandExecutionInfo e) {
		e.bonzi.cooldowns.resetCooldown(this, e.executor.getIdLong());
	}
	public boolean isRegisterable() {
		if(this.adminOnly)
			return false;
		if(this.category == CommandCategory._HIDDEN)
			return false;
		if(args != null && args.args != null)
			for(CommandArg arg: args.args)
				if(arg.type.nativeOption == OptionType.UNKNOWN)
					return false;
		return true;
	}
	/**
	 * Strip special characters for comparison, such as spaces.
	 */
	public String getFilteredCommandName() {
		return name.replaceAll(Constants.WHITESPACE_REGEX, "").toLowerCase();
	}
	/**
	 * same as getFilteredCommandName idk why this exists
	 * @return
	 */
	public String getSlashCommandName() {
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
	
	// The permissions the BOT needs to run this command.
	public Permission[] neededPermissions = new Permission[] { Permission.UNKNOWN };
	
	public void run(CommandExecutionInfo e) {}
}