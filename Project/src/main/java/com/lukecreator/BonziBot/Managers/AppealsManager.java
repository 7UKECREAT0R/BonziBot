package com.lukecreator.BonziBot.Managers;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.IStorableData;

import net.dv8tion.jda.api.entities.Invite.Guild;
import net.dv8tion.jda.api.entities.User;

/**
 * Manages which users have the ability to appeal a ban, aka what's referred to internally as mercy.
 * @author Lukec
 *
 */
public class AppealsManager implements IStorableData {
	
	// Users given a chance to appeal.
	HashMap<Long, List<Long>> mercy = new HashMap<Long, List<Long>>();
	
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
