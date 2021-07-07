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
 * Manages temp-bans for guilds. Permanent bans will not show up here.
 */
public class BanManager implements IStorableData {
	
	public static transient final long NOT_BANNED = -1l;
	
	// Encoded ID, Time of Unban
	public HashMap<Long, HashMap<Long, Long>> data =
		new HashMap<Long, HashMap<Long, Long>>();
	
	public boolean isBanned(Member member) {
		return this.isBanned(member.getGuild().getIdLong(), member.getUser().getIdLong());
	}
	public boolean isBanned(Guild guild, User user) {
		return this.isBanned(guild.getIdLong(), user.getIdLong());
	}
	public boolean isBanned(long guildId, long userId) {
		HashMap<Long, Long> thing = data.get(guildId);
		if(thing == null)
			return false;
		return thing.containsKey(userId);
	}
	public long getBanTime(Member member) {
		return this.getBanTime(member.getGuild().getIdLong(), member.getUser().getIdLong());
	}
	public long getBanTime(Guild guild, User user) {
		return this.getBanTime(guild.getIdLong(), user.getIdLong());
	}
	public long getBanTime(long guildId, long userId) {
		HashMap<Long, Long> thing = data.get(guildId);
		if(thing == null)
			return NOT_BANNED;
		Long obj = thing.get(userId);
		if(obj == null)
			return NOT_BANNED;
		return obj.longValue();
	}
	
	public long unban(Member member) {
		return this.unban(member.getGuild().getIdLong(), member.getUser().getIdLong());
	}
	public long unban(Guild guild, User user) {
		return this.unban(guild.getIdLong(), user.getIdLong());
	}
	public long unban(long guildId, long userId) {
		HashMap<Long, Long> thing = data.get(guildId);
		if(thing == null)
			return NOT_BANNED;
		Long obj = thing.remove(userId);
		if(obj == null)
			return NOT_BANNED;
		return obj.longValue();
	}
	public void ban(Member member, long endsMs) {
		this.ban(member.getGuild().getIdLong(), member.getUser().getIdLong(), endsMs);
	}
	public void ban(Guild guild, User user, long endsMs) {
		this.ban(guild.getIdLong(), user.getIdLong(), endsMs);
	}
	public void ban(long guildId, long userId, long endsMs) {
		HashMap<Long, Long> thing = data.get(guildId);
		if(thing == null)
			thing = new HashMap<Long, Long>();
		thing.put(userId, endsMs);
		this.data.put(guildId, thing);
	}
	
	public List<Map.Entry<Long, Long>> getGuildBans(Guild guild) {
		return this.getGuildBans(guild.getIdLong());
	}
	public List<Map.Entry<Long, Long>> getGuildBans(long guildId) {
		HashMap<Long, Long> thing = data.get(guildId);
		if(thing == null)
			return new HashMap<Long, Long>().entrySet().stream().collect(Collectors.toList());
		return thing.entrySet().stream().collect(Collectors.toList());
	}
	
	@Override
	public void saveData() {
		DataSerializer.writeObject(data, "tempbans");
	}
	@SuppressWarnings("unchecked")
	@Override
	public void loadData() throws EOFException {
		Object o = DataSerializer.retrieveObject("tempbans");
		if(o != null)
			data = (HashMap<Long, HashMap<Long, Long>>)o;
	}
}