package com.lukecreator.BonziBot.Managers;

import java.awt.Color;
import java.io.EOFException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.AllocationList;
import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Data.IStorableData;
import com.lukecreator.BonziBot.Data.Modifier;
import com.lukecreator.BonziBot.Logging.LogButtons;
import com.lukecreator.BonziBot.Logging.LogEntry;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

/**
 * Manages the logs for all guilds including
 *   the storage, buttons, and messages.
 */
public class LoggingManager implements IStorableData {
	
	public static final String FILE_ENTRIES = "log_entries_new";
	public static final int MSG_HISTORY_LEN = 50;
	public static final int LOG_HISTORY_LEN = 50;
	HashMap<Long, Message> exposeData = new HashMap<Long, Message>();
	HashMap<Long, AllocationList<Message>> messages = new HashMap<Long, AllocationList<Message>>();
	HashMap<Long, AllocationList<LogEntry>> entries = new HashMap<Long, AllocationList<LogEntry>>();
	
	/**
	 * Locate a logging channel in this guild.
	 * Might be slow I dunno, just remember to cache.
	 */
	public long findLoggingChannel(Guild g) {
		for(TextChannel tc: g.getTextChannels()) {
			Modifier[] mods = BonziUtils.getChannelModifiers(tc);
			for(Modifier m: mods)
				if(m == Modifier.LOGGING)
					return tc.getIdLong();
		}
		return -1l;
	}
	/**
	 * Returns the cached channel ID of this guild.
	 * Returns -1 if no channel has been cached.
	 */
	public long getLoggingChannelId(Guild g, BonziBot bb) {
		GuildSettings settings = bb.guildSettings.getSettings(g);
		if(settings.loggingEnabled) {
			long id = settings.loggingChannelCached;
			if(id == 0) {
				// Try to relocate.
				id = this.findLoggingChannel(g);
				settings.loggingChannelCached = id;
				bb.guildSettings.setSettings(g, settings);
			}
			return id;
		}
		else return -1l;
	}
	/**
	 * Returns the cached channel of this guild.
	 * Returns null if deleted, uncached, or disabled.
	 */
	public TextChannel getLoggingChannel(Guild g, BonziBot bb) {
		GuildSettings settings = bb.guildSettings.getSettings(g);
		if(settings.loggingEnabled) {
			long id = settings.loggingChannelCached;
			if(id == 0) {
				// Try to relocate.
				id = this.findLoggingChannel(g);
				settings.loggingChannelCached = id;
				bb.guildSettings.setSettings(g, settings);
			}
			return g.getTextChannelById(id);
		} else return null;
	}
	/**
	 * Returns the cached channel of this guild.
	 * Returns null if deleted, uncached, or disabled.
	 */
	public TextChannel getLoggingChannel(long guildId, BonziBot bb, JDA jda) {
		GuildSettings settings = bb.guildSettings.getSettings(guildId);
		if(settings.loggingEnabled) {
			long id = settings.loggingChannelCached;
			Guild guild = jda.getGuildById(guildId);
			if(id == 0) {
				// Try to relocate.
				if(guild != null) {
					id = this.findLoggingChannel(guild);
					settings.loggingChannelCached = id;
					bb.guildSettings.setSettings(guildId, settings);
				}
			}
			return guild.getTextChannelById(id);
		} else return null;
	}
	
	public void tryLog(Guild guild, BonziBot bb, LogEntry entry) {
		TextChannel channel = this.getLoggingChannel(guild, bb);
		if(channel == null)
			return;
		
		this.log(channel, entry);
	}
	public void tryLog(long guildId, BonziBot bb, LogEntry entry, JDA jda) {
		TextChannel channel = this.getLoggingChannel(guildId, bb, jda);
		if(channel == null)
			return;
		
		this.log(channel, entry);
	}
	
	public void log(TextChannel channel, LogEntry entry) {
		long flags = entry.getButtonFlags();
		List<Button> row = new ArrayList<Button>();
		
		if((flags & LogButtons.UNDO.flag) != 0)
			row.add(LogButtons.UNDO_BUTTON);
		if((flags & LogButtons.WARN.flag) != 0)
			row.add(LogButtons.WARN_BUTTON);
		if((flags & LogButtons.MUTE.flag) != 0)
			row.add(LogButtons.MUTE_BUTTON);
		if((flags & LogButtons.KICK.flag) != 0)
			row.add(LogButtons.KICK_BUTTON);
		if((flags & LogButtons.BAN.flag) != 0)
			row.add(LogButtons.BAN_BUTTON);
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(entry.type.color.javaColor);
		MessageEmbed me = entry.toEmbed(eb);
		
		final long guildId = channel.getGuild().getIdLong();
		final AllocationList<LogEntry> list = this.getAllocation(guildId);
		
		// set message id for log entry and add to list
		
		MessageCreateAction action = channel.sendMessageEmbeds(me);
		
		if(!row.isEmpty())
			action = action.setActionRow(row);
		
		action.queue(msg -> {
			long id = msg.getIdLong();
			list.add(entry.withMessageId(id));
		});
	}
	
	public AllocationList<LogEntry> getAllocation(Guild g) {
		return this.getAllocation(g.getIdLong());
	}
	public AllocationList<LogEntry> getAllocation(long guildId) {
		if(this.entries.containsKey(guildId))
			return this.entries.get(guildId);
		AllocationList<LogEntry> list = new
			AllocationList<LogEntry>(LOG_HISTORY_LEN);
		this.entries.put(guildId, list);
		return list;
	}
	AllocationList<Message> getMessageHistory(Guild g) {
		return this.getMessageHistory(g.getIdLong());
	}
	AllocationList<Message> getMessageHistory(long guildId) {
		if(this.messages.containsKey(guildId))
			return this.messages.get(guildId);
		AllocationList<Message> list = new
			AllocationList<Message>(MSG_HISTORY_LEN);
		this.messages.put(guildId, list);
		return list;
	}
	public void addMessageToHistory(Message message, Guild g) {
		this.addMessageToHistory(message, g.getIdLong());
	}
	public void addMessageToHistory(Message message, long guildId) {
		AllocationList<Message> history = this.getMessageHistory(guildId);
		history.add(message);
		this.messages.put(guildId, history);
	}
	public Message getMessageById(long messageId, Guild g) {
		return this.getMessageById(messageId, g.getIdLong());
	}
	public Message getMessageById(long messageId, long guildId) {
		Message message = null;
		
		AllocationList<Message> msgs = this.getMessageHistory(guildId);
		
		if(msgs.isEmpty())
			return null;
		
		for(Message msg: msgs.getArrayList()) {
			long test = msg.getIdLong();
			if(messageId == test) {
				message = msg;
				break;
			}
		}
		
		return message;
	}
	public Message getExposeData(Guild g) {
		return this.getExposeData(g.getIdLong());
	}
	public Message getExposeData(long guildId) {
		return this.exposeData.get(guildId);
	}
	public Message setExposeData(Message m, Guild g) {
		return this.setExposeData(m, g.getIdLong());
	}
	public Message setExposeData(Message m, long guildId) {
		return this.exposeData.put(guildId, m);
	}
	
	public void changeLogChannel(TextChannel potential, BonziBot bb, User executor) {
		Guild guild = potential.getGuild();
		
		EmbedBuilder eb = BonziUtils.quickEmbed
			("Is this a logging channel?",
			"Confirming will enable logging and change this server's logging channel to this one.",
			executor, Color.orange);
		bb.eventWaiter.getConfirmation(executor, (MessageChannelUnion)potential, eb.build(), confirm -> {
			if(!confirm) {
				potential.sendMessageEmbeds(BonziUtils.failureEmbed("Alright, cancelled.")).queue();
				return;
			} else {
				GuildSettings gs = bb.guildSettings.getSettings(guild);
				gs.loggingEnabled = true;
				gs.loggingChannelCached = potential.getIdLong();
				bb.guildSettings.setSettings(guild, gs);
				potential.sendMessageEmbeds(BonziUtils.successEmbed(
					"Alright, this channel is officially the new logging channel!",
					"pro tip: you can do `/serversettings` to change this at any time.")).queue();
			}
		});
	}
	
	@Override
	public void saveData() {
		DataSerializer.writeObject(this.entries, FILE_ENTRIES);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void loadData() throws EOFException {
		Object o = DataSerializer.retrieveObject(FILE_ENTRIES);
		if(o != null)
			this.entries = (HashMap<Long, AllocationList<LogEntry>>)o;
	}
}
