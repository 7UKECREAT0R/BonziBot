package com.lukecreator.BonziBot.Logging;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.Button;

/**
 * Buttons appended to specific log messages.
 * @author Lukec
 */
public enum LogButtons {
	NO_BUTTONS(0, ""),
	UNDO	(1 << 0, "logundo"), 	// 00001
	WARN	(1 << 1, "logwarn"), 	// 00010
	MUTE	(1 << 2, "logmute"), 	// 00100
	KICK	(1 << 3, "logkick"), 	// 01000
	BAN 	(1 << 4, "logban"); 	// 10000
	
	public static final Emoji EMOJI_UNDO = Emoji.fromEmote("punish_undo", 923694865420800070l, false);
	public static final Emoji EMOJI_WARN = Emoji.fromEmote("punish_warn", 923694865429184632l, false);
	public static final Emoji EMOJI_MUTE = Emoji.fromEmote("punish_mute", 923694952351957112l, false);
	public static final Emoji EMOJI_KICK = Emoji.fromEmote("punish_kick", 923694865307549748l, false);
	public static final Emoji EMOJI_BAN = Emoji.fromEmote("punish_ban", 923694865156542486l, false);
	
	public static final Button UNDO_BUTTON = Button.primary(UNDO.protocol, "UNDO").withEmoji(EMOJI_UNDO);
	public static final Button WARN_BUTTON = Button.secondary(WARN.protocol, "WARN").withEmoji(EMOJI_WARN);
	public static final Button MUTE_BUTTON = Button.secondary(MUTE.protocol, "TIMEOUT").withEmoji(EMOJI_MUTE);
	public static final Button KICK_BUTTON = Button.danger(KICK.protocol, "KICK").withEmoji(EMOJI_KICK);
	public static final Button BAN_BUTTON = Button.danger(BAN.protocol, "BAN").withEmoji(EMOJI_BAN);
	
	public final long flag;
	public final String protocol;
	private LogButtons(long flag, String protocol) {
		this.flag = flag;
		this.protocol = protocol;
	}
	/**
	 * Test if this button is included in the flags.
	 */
	boolean inFlags(long flags) {
		return (flags & this.flag) != 0;
	}
	long merge(LogButtons other) {
		return this.flag | other.flag;
	}
	long merge(LogButtons...buttons) {
		long l = this.flag;
		for(LogButtons b: buttons)
			l |= b.flag;
		return l;
	}
	
	public static boolean isLogProtocol(String actionId) {
		return actionId.startsWith("log");
	}
}
