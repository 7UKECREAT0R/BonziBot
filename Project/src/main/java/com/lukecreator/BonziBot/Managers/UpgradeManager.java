package com.lukecreator.BonziBot.Managers;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.IStorableData;

import net.dv8tion.jda.api.entities.Invite.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

/*
 * Manages everything related to upgrades.
 */
public class UpgradeManager implements IStorableData {
	
	public HashMap<Long, Integer> upgrades = new HashMap<Long, Integer>();
	public HashMap<Long, List<Long>> upgraders = new HashMap<Long, List<Long>>();
	
	// Getters / Setters
	public int getUpgrades(Guild g) {
		return getUpgrades(g.getIdLong());
	}
	public int getUpgrades(long guildId) {
		if(upgrades.containsKey(guildId))
			return upgrades.get(guildId);
		upgrades.put(guildId, 0);
		return 0;
	}
	public void setUpgrades(Guild g, int amount) {
		setUpgrades(g.getIdLong(), amount);
	}
	public void setUpgrades(long guildId, int amount) {
		upgrades.put(guildId, amount);
	}
	public void addUpgrade(Guild g) {
		addUpgrade(g.getIdLong());
	}
	public void addUpgrade(long guildId) {
		int ups = 0;
		if(upgrades.containsKey(guildId))
			ups = upgrades.get(guildId);
		upgrades.put(guildId, ++ups);
	}
	
	// Upgrade Methods
	public void upgradeAsUser(Member member) {
		upgradeAsUser(
			member.getGuild().getIdLong(),
			member.getUser().getIdLong());
	}
	public void upgradeAsUser(User u, Guild g) {
		upgradeAsUser(g.getIdLong(), u.getIdLong());
	}
	public void upgradeAsUser(long guildId, long userId) {
		
		addUpgrade(guildId);
		
		boolean alreadyUpgrader = false;
		
		List<Long> ulist;
		if(upgraders.containsKey(guildId))
			ulist = upgraders.get(guildId);
		else ulist = new ArrayList<Long>();
		
		if(!alreadyUpgrader)
		for(long id: ulist)
		if(id == userId) {
			alreadyUpgrader = true;
			break;
		}
		
		if(!alreadyUpgrader)
			ulist.add(userId);
		upgraders.put(guildId, ulist);
	}
	
	// Upgrader Methods
	public boolean userIsUpgrader(Member m) {
		return userIsUpgrader
			(m.getUser().getIdLong(),
			m.getGuild().getIdLong());
	}
	public boolean userIsUpgrader(User u, Guild g) {
		return userIsUpgrader(u.getIdLong(), g.getIdLong());
	}
	public boolean userIsUpgrader(long userId, long guildId) {
		List<Long> ups = getUpgradersForGuild(guildId);
		if(ups.isEmpty()) return false;
		
		for(long id: ups) {
			if(id == userId)
				return true;
		}
		return false;
	}
	public List<Long> getUpgradersForGuild(Guild g) {
		return getUpgradersForGuild(g.getIdLong());
	}
	public List<Long> getUpgradersForGuild(long guildId) {
		if(upgraders.containsKey(guildId))
			return upgraders.get(guildId);
		else {
			List<Long> l = new ArrayList<Long>();
			upgraders.put(guildId, l);
			return l;
		}
	}
	
	@Override
	public void saveData() {
		DataSerializer.writeObject(upgrades, "upgrades.ser");
		DataSerializer.writeObject(upgraders, "upgraders.ser");
	}
	@SuppressWarnings("unchecked")
	@Override
	public void loadData() throws EOFException {
		
		Object o1 = DataSerializer.retrieveObject("upgrades.ser");
		Object o2 = DataSerializer.retrieveObject("upgraders.ser");
		if(o1 != null)
			upgrades = (HashMap<Long, Integer>) o1;
		if(o2 != null)
			upgraders = (HashMap<Long, List<Long>>) o2;
	}
}
