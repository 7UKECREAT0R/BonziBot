package com.lukecreator.BonziBot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.User;

/**
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * THIS IS LEGACY CODE FOR THE PURPOSE OF CONVERTING DATA
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
public class UserProfile implements Serializable {

	private static final long serialVersionUID = 7353907761035818437L;
	public List<Warn> warns;
	int xp; // This is now deprecated, use global (HashMap<Long, Integer> xp) instead.
	
	public void CheckVersion() {
		if(xp == 0) {
			xp = 1;
		}
		if(warns == null) {
			warns = new ArrayList<Warn>();
		}
	}
	public UserProfile() {
		warns = new ArrayList<Warn>();
		xp = 1;
	}
	
	public void addWarn(Warn w) {
		warns.add(w);
	}
	public void addWarn(String reason, User who) {
		Warn w = new Warn(who, reason);
		warns.add(w);
	}
	public boolean removeWarn(Warn w) {
		return warns.remove(w);
	}
	/***
	 * Returns the amount of warns removed.
	 * @param reason
	 * @return
	 */
	public int removeWarns(String reason) {
		int count = 0;
		List<Warn> toRemove =
			new ArrayList<Warn>();
		for(Warn w: warns) {
			if(w.reason.equalsIgnoreCase(reason)) {
				toRemove.add(w);
				count++;
			}
		}
		for(Warn w: toRemove) {
			removeWarn(w);
		}
		return count;
	}
	public void clearWarns() {
		warns.clear();
	}
	
}
