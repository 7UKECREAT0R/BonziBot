package com.lukecreator.BonziBot.Data;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;

import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;

public class LogEntry {
	
	public enum Buttons {
		UNDO(1 << 0), // 00001
		WARN(1 << 1), // 00010
		MUTE(1 << 2), // 00100
		KICK(1 << 3), // 01000
		BAN (1 << 4); // 10000
		
		public final long flag;
		Buttons(long flag) {
			this.flag = flag;
		}
		/**
		 * Test if this button is included in the flags.
		 */
		boolean inFlags(long flags) {
			return (flags & this.flag) > 0;
		}
		long merge(Buttons other) {
			return this.flag | other.flag;
		}
		long merge(Buttons...buttons) {
			long l = this.flag;
			for(Buttons b: buttons)
				l |= b.flag;
			return l;
		}
	}
	public enum Type {
		MUTE,
		UNMUTE,
		BAN,
		UNBAN,
		WARN,
		REMOVEWARN,
		CLEARWARNS,
		CLEARCOMMAND,
		DELETEDMESSAGE,
		SWEARMESSAGE,
		SUSMESSAGE, // amog us
		NICKNAMECHANGE,
		PREFIXCHANGE,
		TEXTCHANNELCREATE,
		TEXTCHANNELREMOVE,
		VOICECHANNELCREATE,
		VOICECHANNELREMOVE
	}
	
	public Type type; // The type of action performed.
	public long buttonFlags; // The buttons to add.
	public long initiator, target; // The executor and target.
	public long id, timestamp;	// The unique ID of the entry and timestamp of creation.
	public String reason; // The reason for the action.
	
	// Extra data.
	public String extString;
	public String extString2;
	public long extLong;
	public long extLong2;
	
	/**
	 * WHOA this constructor is CRAZY
	 */
	public LogEntry(Type type, long buttonFlags, long initiator, long target, @Nullable String reason, @Nullable String extString, @Nullable String extString2, long extLong, long extLong2) {
		this.type = type;
		this.buttonFlags = buttonFlags;
		this.initiator = initiator;
		this.target = target;
		this.reason = reason;
		this.extString = extString;
		this.extString2 = extString2;
		this.extLong = extLong;
		this.extLong2 = extLong2;
		this.id = BonziUtils.generateId();
		this.timestamp = System.currentTimeMillis();
	}
	
	// Factory Methods but plot twist they
	// consult audits now so it has to be
	// async!!! :cryig:
	
	/**
	 * Executor: The user who created the channel.
	 * Target: The channel ID.
	 * Reason: none
	 * Extra Strings: none, none
	 * Extra Data:    none, none
	 */
	public static void fromEvent(BonziBot bb, TextChannelCreateEvent event, Consumer<LogEntry> entryOutput) {
		event.getGuild()
		.retrieveAuditLogs()
		.limit(1)
		.type(ActionType.CHANNEL_CREATE)
		.queue(ale -> {
			long userId = -1l;
			if(!ale.isEmpty()) {
				AuditLogEntry entry = ale.get(0);
				if(entry.getUser() != null)
					userId = entry.getUser().getIdLong();
			}
			entryOutput.accept(new LogEntry(
				Type.TEXTCHANNELCREATE,
				Buttons.UNDO.flag,
				userId, // Executor
				event.getChannel().getIdLong(), // Target
				null, // Reason
				null, null, // Extra Strings
				0l, 0l)); // Extra Longs
		});
	}
	
	/**
	 * Executor: The user who created the channel.
	 * Target: The channel ID.
	 * Reason: none
	 * Extra Strings: Text Channel Name, Text Channel Topic
	 * Extra Data:    Text Channel Slowmode, none
	 */
	public static void fromEvent(BonziBot bb, TextChannelDeleteEvent event, Consumer<LogEntry> entryOutput) {
		event.getGuild()
		.retrieveAuditLogs()
		.limit(1)
		.type(ActionType.CHANNEL_DELETE)
		.queue(ale -> {
			long userId = -1l;
			if(!ale.isEmpty()) {
				AuditLogEntry entry = ale.get(0);
				if(entry.getUser() != null)
					userId = entry.getUser().getIdLong();
			}
			TextChannel tc = event.getChannel();
			entryOutput.accept(new LogEntry(
				Type.TEXTCHANNELREMOVE,
				Buttons.UNDO.flag,
				userId, // Executor
				event.getChannel().getIdLong(), // Target
				null, // Reason
				tc.getName(), tc.getTopic(), // Extra Strings
				tc.getSlowmode(), 0l)); // Extra Longs
		});
	}
	
	/**
	 * Executor: The user who was nicknamed.
	 * Target: The user who was nicknamed.
	 * Reason: none
	 * Extra Strings: Old Nickname, New Nickname
	 * Extra Data:    none, none
	 */
	public static void fromEvent(BonziBot bb, GuildMemberUpdateNicknameEvent event, Consumer<LogEntry> entryOutput) {
		Member member = event.getEntity();
		User user = member.getUser();
		long userId = user.getIdLong();
		
		// HOLY FRICK IM GONNA HAVE A SEIZURE
		entryOutput.accept(new LogEntry(Type.NICKNAMECHANGE, Buttons.UNDO.flag | Buttons.WARN.flag,
			userId, userId, null, event.getOldNickname(), event.getNewNickname(), 0l, 0l));
	}
	
	/**
	 * Executor: none
	 * Target: The ID of the deleted message.
	 * Reason: Sent a message: "content"
	 * Extra Strings: Message Content, Message Author Tag
	 * Extra Data:    Message Attachment Length, Message Author ID
	 */
	public static void fromEvent(BonziBot bb, GuildMessageDeleteEvent event, Consumer<LogEntry> entryOutput) {
		long messageId = event.getMessageIdLong();
		long guildId = event.getGuild().getIdLong();
		Message cached = bb.logging.getMessageById(messageId, guildId);
		
		if(cached == null)
			return;
		
		String reason = "Sent a message: \"" + cached.getContentDisplay() + "\"";
		entryOutput.accept(new LogEntry(Type.DELETEDMESSAGE,
			Buttons.WARN.flag | Buttons.KICK.flag, 0l, messageId, reason,
			cached.getContentRaw(), cached.getAuthor().getAsTag(),
			cached.getAttachments().size(), cached.getAuthor().getIdLong()));
	}
	
	/**
	 * Executor: The user who performed the ban.
	 * Target: The user who was nicknamed.
	 * Reason: The reason.
	 * Extra Strings: Banner Name, Banned Name
	 * Extra Data:    none,        none
	 */
	public static void fromEvent(BonziBot bb, GuildBanEvent event, Consumer<LogEntry> entryOutput) {
		User target = event.getUser();
		event.getGuild()
		.retrieveAuditLogs()
		.limit(1)
		.type(ActionType.BAN)
		.queue(ale -> {
			if(!ale.isEmpty()) {
				AuditLogEntry entry = ale.get(0);
				String reason = entry.getReason();
				User executor = entry.getUser();
				if(reason == null)
					reason = "(No reason specified.)";
				long executorId = (executor == null)
					? 0l : executor.getIdLong();
				long bannedId = target.getIdLong();
				String executorName = (executor == null)
					? "Couldn't find name." : executor.getAsTag();
				String targetName = target.getAsTag();
				entryOutput.accept(new LogEntry(
						Type.BAN,
						Buttons.UNDO.flag,
						executorId, // Executor
						bannedId, // Target
						reason, // Reason
						executorName, targetName, // Extra Strings
						0l, 0l)); // Extra Longs
				return;
			}
			// Nothing found.
			entryOutput.accept(new LogEntry(
					Type.BAN,
					Buttons.UNDO.flag,
					0l, // Executor
					target.getIdLong(), // Target
					"Error getting information.", // Reason
					"Can't find the banner.", target.getAsTag(), // Extra Strings
					0l, 0l)); // Extra Longs
			return;
		});
	}
	/**
	 * Executor: none
	 * Target: The user who was unbanned.
	 * Reason: none
	 * Extra Strings: User Tag, none
	 * Extra Data:    none,     none
	 */
	public static void fromEvent(BonziBot bb, GuildUnbanEvent event, Consumer<LogEntry> entryOutput) {
		entryOutput.accept(new LogEntry(
				Type.BAN,
				Buttons.UNDO.flag,
				0l, // Executor
				event.getUser().getIdLong(), // Target
				null, // Reason
				event.getUser().getAsTag(), null, // Extra Strings
				0l, 0l)); // Extra Longs
		return;
	}
}