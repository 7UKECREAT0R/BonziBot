package com.lukecreator.BonziBot.Data;

import java.io.Serializable;

/**
 * Badges for profiles.
 */
public class Badge implements Serializable {
	
	private static final long serialVersionUID = 1448244096343021881L;
	
	public final String name;
	public final String desc;
	public final GenericEmoji icon;
	
	private Badge(String name, String desc, GenericEmoji icon) {
		this.name = name;
		this.desc = desc;
		this.icon = icon;
	}
	
	public static final Badge FRIEND = new Badge(
		"Friend", "Friend of the developers!", GenericEmoji.fromEmoji("❤️"));
	public static final Badge DEVELOPER = new Badge(
		"Developer", "Contributed/helped make BonziBot!", GenericEmoji.fromEmoji("💻"));
	public static final Badge ACHIEVEMENT_MASTER = new Badge(
		"Achievement Master", "Collected All Achievements!", GenericEmoji.fromEmoji("🎖️"));
	public static final Badge LOTTERY_WINNER = new Badge(
		"Lottery Winner", "Won the lottery!", GenericEmoji.fromEmoji("🤑"));
	public static final Badge BUG_HUNTER = new Badge(
		"Bug Hunter", "Submitted a valid bug report!", GenericEmoji.fromEmoji("🪲"));
	public static final Badge CREATIVE = new Badge(
		"Creative", "Submitted an awesome idea that got accepted!", GenericEmoji.fromEmoji("🧠"));
}
