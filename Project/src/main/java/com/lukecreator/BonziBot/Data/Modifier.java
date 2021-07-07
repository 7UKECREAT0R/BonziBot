package com.lukecreator.BonziBot.Data;

public enum Modifier {
	NO_XP("❌", "Disables the obtaining of XP in the channel. Use this for things like spam channels."),
	NO_FILTER("🤬", "Disables filtering in the channel. Also disabled in NSFW channels automatically."),
	NO_MUSIC("🎵", "Disables music commands. Good if you don't want a channel to be flooded with bot spam."),
	BOT_COMMANDS("🤖", "If bot commands are disabled, then channels with this modifier will allow them."),
	PICTURES_ONLY("🖼️", "Only allows messages with attachments. Make a showcase channel!"),
	PREMIUM_ONLY("🏆", "Only lets users that have BonziBot Premium chat in the channel."),
	ANONYMOUS("🕵️‍♂️", "Every message sent in the channel will be anonymous!"),
	LOGGING("🗒️", "Puts log UIs into this channel. Can be used to swiftly punish users!"),
	COUNTING("🎰", "Let users collaborate and count as high as they can! Maybe even get onto the leaderboard?"),
	RPG("⚔️", "A full role-playing game with pvp, tons of items, and more! WORK IN PROGRESS."),
	NO_EXPOSE("🤐", "Messages deleted in this channel can not be exposed by users. Good for private staff channels.");
	
	public String icon;
	public String desc;
	
	Modifier(String icon, String desc) {
		this.icon = icon;
		this.desc = desc;
	}
	public String getDisplayName() {
		return this.name().replaceAll("_", " ").toLowerCase();
	}
	public String getCompareName() {
		return this.name().replaceAll("_", "").toUpperCase();
	}
}
