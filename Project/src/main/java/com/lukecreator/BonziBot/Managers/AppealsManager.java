package com.lukecreator.BonziBot.Managers;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lukecreator.BonziBot.Data.BanAppeal;
import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.IStorableData;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

/**
 * Manages which users have the ability to appeal a ban, referred to internally as mercy.
 * This also holds data on existing appeals waiting to be accepted/denied. Any garbage made
 * shouldn't be an issue for years to come as the data structures are quite small.
 * @author Lukec
 *
 */
public class AppealsManager implements IStorableData {
	
	// Users given a chance to appeal.
	HashMap<Long, List<Long>> mercy = new HashMap<Long, List<Long>>();
	HashMap<Long, List<BanAppeal>> appeals = new HashMap<Long, List<BanAppeal>>();
	
	public boolean hasMercy(Guild guild, User user) {
		return this.hasMercy(guild.getIdLong(), user.getIdLong());
	}
	public boolean hasMercy(long guild, long user) {
		List<Long> m = mercy.get(guild);
		if(m == null)
			return false;
		for(long test: m) {
			if(user == test)
				return true;
		}
		return false;
	}
	public List<Long> getMercy(Guild guild) {
		return this.getMercy(guild.getIdLong());
	}
	public List<Long> getMercy(long guild) {
		return mercy.get(guild);
	}
	public void mercy(Guild guild, User user) {
		this.mercy(guild.getIdLong(), user.getIdLong());
	}
	public void mercy(long guild, long user) {
		List<Long> m = mercy.get(guild);
		if(m == null)
			m = new ArrayList<Long>();
		m.add(user);
		mercy.put(guild, m);
	}
	public void noMercy(Guild guild, User user) {
		this.noMercy(guild.getIdLong(), user.getIdLong());
	}
	public void noMercy(long guild, long user) {
		List<Long> m = mercy.get(guild);
		if(m == null)
			return;
		for(int i = 0; i < m.size(); i++) {
			long test = m.get(i);
			if(user == test) {
				m.remove(i);
				break;
			}
		}
		mercy.put(guild, m);
	}
	
	public void addAppeal(Guild guild, BanAppeal appeal) {
		this.addAppeal(guild.getIdLong(), appeal);
	}
	public void addAppeal(long guild, BanAppeal appeal) {
		List<BanAppeal> existing = appeals.get(guild);
		if(existing == null)
			existing = new ArrayList<BanAppeal>();
		
		existing.add(appeal);
		appeals.put(guild, existing);
	}
	public BanAppeal getAppeal(Guild guild, long bannedUser) {
		return this.getAppeal(guild.getIdLong(), bannedUser);
	}
	public BanAppeal getAppeal(long guild, long bannedUser) {
		if(appeals.containsKey(guild)) {
			List<BanAppeal> existing = appeals.get(guild);
			for(int i = 0; i < existing.size(); i++) {
				BanAppeal test = existing.get(i);
				if(test.userId == bannedUser)
					return test;
			}
		}
		return null;
	}
	public BanAppeal removeAppeal(Guild guild, long bannedUser) {
		return this.removeAppeal(guild.getIdLong(), bannedUser);
	}
	public BanAppeal removeAppeal(long guild, long bannedUser) {
		if(appeals.containsKey(guild)) {
			List<BanAppeal> existing = appeals.get(guild);
			for(int i = 0; i < existing.size(); i++)
				if(existing.get(i).userId == bannedUser)
					return existing.remove(i);
		}
		return null;
	}
	
	
	@Override
	public void saveData() {
		DataSerializer.writeObject(mercy, "mercy");
	}
	@SuppressWarnings("unchecked")
	@Override
	public void loadData() throws EOFException {
		Object o = DataSerializer.retrieveObject("mercy");
		if(o == null)
			return;
		
		mercy = (HashMap<Long, List<Long>>)o;
	}
}
