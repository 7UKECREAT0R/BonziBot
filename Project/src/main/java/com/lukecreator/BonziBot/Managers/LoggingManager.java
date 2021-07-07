package com.lukecreator.BonziBot.Managers;

import java.awt.Color;
import java.io.EOFException;
import java.util.HashMap;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.AllocationList;
import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Data.IStorableData;
import com.lukecreator.BonziBot.Data.LogEntry;
import com.lukecreator.BonziBot.Data.Modifier;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

/**
 * Manages the logs for all guilds including
 *   the storage, reactions, and messages.
 */
public class LoggingManager implements IStorableData {
	
	public static final String FILE_ENTRIES = "log_entries";
	public static final int MSG_HISTORY_LEN = 250;
	public static final int LOG_HISTORY_LEN = 100;
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
	
	AllocationList<LogEntry> getAllocation(Guild g) {
		return getAllocation(g.getIdLong());
	}
	AllocationList<LogEntry> getAllocation(long guildId) {
		if(entries.containsKey(guildId))
			return entries.get(guildId);
		AllocationList<LogEntry> list = new
			AllocationList<LogEntry>(LOG_HISTORY_LEN);
		entries.put(guildId, list);
		return list;
	}
	AllocationList<Message> getMessageHistory(Guild g) {
		return getMessageHistory(g.getIdLong());
	}
	AllocationList<Message> getMessageHistory(long guildId) {
		if(messages.containsKey(guildId))
			return messages.get(guildId);
		AllocationList<Message> list = new
			AllocationList<Message>(MSG_HISTORY_LEN);
		messages.put(guildId, list);
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
		return getMessageById(messageId, g.getIdLong());
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
		return getExposeData(g.getIdLong());
	}
	public Message getExposeData(long guildId) {
		return exposeData.get(guildId);
	}
	public Message setExposeData(Message m, Guild g) {
		return setExposeData(m, g.getIdLong());
	}
	public Message setExposeData(Message m, long guildId) {
		return exposeData.put(guildId, m);
	}
	
	public void log(LogEntry entry, Guild guild, BonziBot bb) {
		
	}
	public void changeLogChannel(TextChannel potential, BonziBot bb, User executor) {
		Guild guild = potential.getGuild();
		
		EmbedBuilder eb = BonziUtils.quickEmbed
			("Is this a logging channel?",
			"Confirming will enable logging and change this server's logging channel to this one.",
			executor, Color.orange);
		bb.eventWaiter.getConfirmation(executor, potential, eb.build(), confirm -> {
			if(!confirm) {
				potential.sendMessage(BonziUtils.failureEmbed("Alright, cancelled.")).queue();
				return;
			} else {
				String prefix = bb.guildSettings.getSettings(guild).getPrefix();
				GuildSettings gs = bb.guildSettings.getSettings(guild);
				gs.loggingEnabled = true;
				gs.loggingChannelCached = potential.getIdLong();
				bb.guildSettings.setSettings(guild, gs);
				potential.sendMessage(BonziUtils.successEmbed(
					"Alright, this channel is officially the new logging channel!",
					"pro tip: you can do `" + prefix + "serversettings` to change this at any time.")).queue();
			}
		});
	}
	
	@Override
	public void saveData() {
		DataSerializer.writeObject(entries, FILE_ENTRIES);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void loadData() throws EOFException {
		Object o = DataSerializer.retrieveObject(FILE_ENTRIES);
		if(o != null)
			entries = (HashMap<Long, AllocationList<LogEntry>>)o;
	}
}
