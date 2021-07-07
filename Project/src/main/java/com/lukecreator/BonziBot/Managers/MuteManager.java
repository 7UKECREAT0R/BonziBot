package com.lukecreator.BonziBot.Managers;

import java.io.EOFException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.IStorableData;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

/*
 * Manages all mutes for guilds.
 */
public class MuteManager implements IStorableData {
	
	// If expire time is PERM then the mute is forever or until manually unmuted.
	public static transient final long PERM = 0l;
	public static transient final long NOT_MUTED = -1l;
	
	// Encoded ID, Time of Unmute
	public HashMap<Long, HashMap<Long, Long>> data =
		new HashMap<Long, HashMap<Long, Long>>();
	
	public boolean isMuted(Member member) {
		return this.isMuted(member.getGuild().getIdLong(), member.getUser().getIdLong());
	}
	public boolean isMuted(Guild guild, User user) {
		return this.isMuted(guild.getIdLong(), user.getIdLong());
	}
	public boolean isMuted(long guildId, long userId) {
		HashMap<Long, Long> thing = data.get(guildId);
		if(thing == null)
			return false;
		return thing.containsKey(userId);
	}
	public long getMuteTime(Member member) {
		return this.getMuteTime(member.getGuild().getIdLong(), member.getUser().getIdLong());
	}
	public long getMuteTime(Guild guild, User user) {
		return this.getMuteTime(guild.getIdLong(), user.getIdLong());
	}
	public long getMuteTime(long guildId, long userId) {
		HashMap<Long, Long> thing = data.get(guildId);
		if(thing == null)
			return NOT_MUTED;
		Long obj = thing.get(userId);
		if(obj == null)
			return NOT_MUTED;
		return obj.longValue();
	}
	
	public long unmute(Member member) {
		return this.unmute(member.getGuild().getIdLong(), member.getUser().getIdLong());
	}
	public long unmute(Guild guild, User user) {
		return this.unmute(guild.getIdLong(), user.getIdLong());
	}
	public long unmute(long guildId, long userId) {
		HashMap<Long, Long> thing = data.get(guildId);
		if(thing == null)
			return NOT_MUTED;
		Long obj = thing.remove(userId);
		if(obj == null)
			return NOT_MUTED;
		return obj.longValue();
	}
	public void mute(Member member) {
		this.mute(member.getGuild().getIdLong(), member.getUser().getIdLong(), PERM);
	}
	public void mute(Guild guild, User user) {
		this.mute(guild.getIdLong(), user.getIdLong(), PERM);
	}
	public void mute(long guildId, long userId) {
		HashMap<Long, Long> thing = data.get(guildId);
		if(thing == null)
			thing = new HashMap<Long, Long>();
		thing.put(userId, PERM);
		this.data.put(guildId, thing);
	}
	public void mute(Member member, long endsMs) {
		this.mute(member.getGuild().getIdLong(), member.getUser().getIdLong(), endsMs);
	}
	public void mute(Guild guild, User user, long endsMs) {
		this.mute(guild.getIdLong(), user.getIdLong(), endsMs);
	}
	public void mute(long guildId, long userId, long endsMs) {
		HashMap<Long, Long> thing = data.get(guildId);
		if(thing == null)
			thing = new HashMap<Long, Long>();
		thing.put(userId, endsMs);
		this.data.put(guildId, thing);
	}
	
	public List<Map.Entry<Long, Long>> getGuildMutes(Guild guild) {
		return this.getGuildMutes(guild.getIdLong());
	}
	public List<Map.Entry<Long, Long>> getGuildMutes(long guildId) {
		HashMap<Long, Long> thing = data.get(guildId);
		if(thing == null)
			return new HashMap<Long, Long>().entrySet().stream().collect(Collectors.toList());
		return thing.entrySet().stream().collect(Collectors.toList());
	}
	
	@Override
	public void saveData() {
		DataSerializer.writeObject(data, "mutes");
	}
	@SuppressWarnings("unchecked")
	@Override
	public void loadData() throws EOFException {
		Object o = DataSerializer.retrieveObject("mutes");
		if(o != null)
			data = (HashMap<Long, HashMap<Long, Long>>)o;
	}
}