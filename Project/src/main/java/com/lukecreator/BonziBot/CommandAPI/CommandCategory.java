package com.lukecreator.BonziBot.CommandAPI;

public enum CommandCategory {
	
	//   Will always show at the
	// top level of the help menu.
	_TOPLEVEL("Top Level Commands"),
	
	// Hidden from the help menu.
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
