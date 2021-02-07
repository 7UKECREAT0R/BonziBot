package com.lukecreator.BonziBot.CommandAPI;

public enum CommandCategory {
	
	// Both hidden from the help menu.
	_SHOP_COMMAND("Shop Commands for Purchase"),
	_HIDDEN("Hidden Commands"),
	
	FUN("Fun Commands"),
	COINS("Coin Commands"),
	MODERATION("Moderation Commands"),
	UTILITIES("Utility Commands"),
	MUSIC("Music Commands"),
	UPGRADE("Upgrade Commands");

	public final String name;
	private CommandCategory(String string) {
		this.name = string;
	}
}
