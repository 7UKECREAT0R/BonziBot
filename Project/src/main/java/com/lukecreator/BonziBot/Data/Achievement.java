package com.lukecreator.BonziBot.Data;

import java.io.Serializable;

public class Achievement implements Serializable {

	private static final long serialVersionUID = -7834517948670849883L;
	
	public final String name;
	public final String desc;
	public final GenericEmoji icon;
	public final int rarity;
	
	private Achievement(String name, String desc, GenericEmoji icon, int rarity) {
		this.name = name;
		this.desc = desc;
		this.icon = icon;
		this.rarity = rarity;
	}
	
	public static final Achievement GENEROUS = new Achievement(
		"Generous", "Upgrade someone's server.", GenericEmoji.fromEmoji("😇"), 1);
	public static final Achievement MILESTONER = new Achievement(
		"Milestoner", "Count a thousandth number in counting game.", GenericEmoji.fromEmoji("⌨️"), 3);
	public static final Achievement LUCKY = new Achievement(
		"Lucky", "Win a chance command with 1,000 coins or more.", GenericEmoji.fromEmoji("🪙"), 3);
	public static final Achievement LUCK_MASTER = new Achievement(
		"Luck Master", "Win a chance command with 10,000 coins or more!", GenericEmoji.fromEmoji("⌨️"), 4);
	public static final Achievement GOOD_ADMIN = new Achievement(
		"Good Admin", "Backup a server.", GenericEmoji.fromEmoji("👮"), 1);
	public static final Achievement SNAZZY = new Achievement(
		"Snazzy", "Update your profile!", GenericEmoji.fromEmoji("🤩"), 1);
	public static final Achievement RPG_PLAYER = new Achievement(
		"RPG Player", "Start your adventure in BonziBot RPG.", GenericEmoji.fromEmoji("👶"), 1);
	public static final Achievement BLOODTHIRSTY = new Achievement(
		"Bloodthirsty", "Kill fifty players in RPG.", GenericEmoji.fromEmoji("🩸"), 2);
	public static final Achievement OVERPOWERED = new Achievement(
		"Overpowered", "Deal over a hundred damage in a single hit in RPG!", GenericEmoji.fromEmoji("⚔️"), 3);
	public static final Achievement SPY = new Achievement(
		"The Spy", "Expose a message.", GenericEmoji.fromEmoji("🕵️"), 1);
}
