package com.lukecreator.BonziBot.Logging;

import java.awt.Color;
import java.io.Serializable;
import java.util.function.Consumer;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Commands.WarnCommand;
import com.lukecreator.BonziBot.Data.ModernWarn;
import com.lukecreator.BonziBot.Data.UserAccount;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Footer;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

/**
 * Abstraction of a "log entry"
 * @author Lukec
 */
public abstract class LogEntry implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public enum LogColor {
		BONZI_RELATED(161, 86, 184),	// BonziBot related.
		LOG(97, 97, 97),				// Simple log of an everyday action.
		MINOR_OFFENSE(224, 117, 117),	// Minor offense.
		MAJOR_OFFENSE(133, 30, 30),		// Major offense.
		
		SIMPLE_CHANGE(112, 207, 197),	// Simple change to the server.
		SIMPLE_PUNISHMENT(209, 156, 86),// Simple or unimportant punishment.
		SIMPLE_ACTION(214, 107, 184),	// Simple action performed.
		
		LARGE_CHANGE(29, 173, 10),		// Important/big change to the server.
		LARGE_PUNISHMENT(232, 110, 16),	// Important/big punishment given.
		LARGE_ACTION(201, 26, 152);		// Important/big action performed.
		
		public final Color javaColor;
		
		LogColor(int r, int g, int b) {
			this.javaColor = new Color(r, g, b);
		}
	}
	public enum Type {
		MUTE(LogColor.SIMPLE_PUNISHMENT),
		UNMUTE(LogColor.SIMPLE_PUNISHMENT),
		BAN(LogColor.LARGE_PUNISHMENT),
		BANTEMP(LogColor.LARGE_PUNISHMENT),
		UNBAN(LogColor.LARGE_ACTION),
		WARN(LogColor.SIMPLE_PUNISHMENT),
		REMOVEWARN(LogColor.SIMPLE_ACTION),
		CLEARWARNS(LogColor.LARGE_ACTION),
		CLEARCOMMAND(LogColor.LARGE_ACTION),
		DELETEDMESSAGE(LogColor.LOG),
		NICKNAMECHANGE(LogColor.LOG),
		SWEARMESSAGE(LogColor.MAJOR_OFFENSE),
		SUSMESSAGE(LogColor.MINOR_OFFENSE), // amog us (this is for u danny)
		USERLEFT(LogColor.LOG),
		USERJOINED(LogColor.LOG),
		TEXTCHANNELCREATE(LogColor.SIMPLE_ACTION),
		TEXTCHANNELREMOVE(LogColor.SIMPLE_ACTION),
		VOICECHANNELCREATE(LogColor.SIMPLE_ACTION),
		VOICECHANNELREMOVE(LogColor.SIMPLE_ACTION);
		
		public final LogColor color;
		Type(LogColor color) {
			this.color = color;
		}
	}
	
	long buttonFlags;
	public long getButtonFlags() {
		return this.buttonFlags;
	}
	
	public final LogEntry.Type type;
	public final long timestamp;
	public long messageId;
	
	public LogEntry(LogEntry.Type type) {
		this.type = type;
		this.timestamp = System.currentTimeMillis();
	}
	public LogEntry withMessageId(long messageId) {
		this.messageId = messageId;
		return this;
	}
	
	@Override
	public String toString() {
		return type.toString() + ", messageId: " + messageId + ", self: " + this.getClass().getName();
	}
	
	/**
	 * Helper method to set the footer of the original embed. Appends on if one already exists.
	 * @param hook
	 * @param footer
	 */
	public static void setOriginalFooter(ButtonClickEvent event, String footer) {
		event.getHook().retrieveOriginal().queue(msg -> {
			MessageEmbed me = msg.getEmbeds().get(0);
			EmbedBuilder eb = new EmbedBuilder(me);
			Footer old = me.getFooter();
			
			String complete;
			if(old != null)
				complete = old.getText() + "\n" + footer;
			else
				complete = footer;
			
			eb.setFooter(complete);
			event.getHook().editOriginalEmbeds(eb.build()).queue();
		});
	}
	/**
	 * Warn a user and send them a proper DM, if enabled.
	 * @param hook
	 * @param footer
	 */
	public static void warnUser(UserAccount account, User user, Guild guild, String reason) {
		ModernWarn warn = new ModernWarn(reason, guild);
		account.addWarn(warn);
		
		if(account.optOutDms)
			return;
		
		// dm a warning message
		MessageEmbed me = WarnCommand.formDM(guild, warn);
		BonziUtils.messageUser(user, me);
	}
	
	/**
	 * Build a MessageEmbed filled with the correct data for this log entry.
	 * @return null if this entry should not get logged.
	 */
	public abstract MessageEmbed toEmbed(EmbedBuilder input);
	/**
	 * Asynchronously check and load data from the correct event as per specified by the {@link Type} on this entry.
	 * @param dataStructure The object to load from. Check implementation for details.
	 * @param bb The BonziBot object to be used to check Bonzi-related fields.
	 */
	public abstract void loadData(Object dataStructure, BonziBot bb, Consumer<LogEntry> _success, Consumer<Throwable> _failure);

	// Actions
	public abstract void performActionUndo(BonziBot bb, ButtonClickEvent event);
	public abstract void performActionWarn(BonziBot bb, ButtonClickEvent event);
	public abstract void performActionMute(BonziBot bb, ButtonClickEvent event);
	public abstract void performActionKick(BonziBot bb, ButtonClickEvent event);
	public abstract void performActionBan(BonziBot bb, ButtonClickEvent event);
}
