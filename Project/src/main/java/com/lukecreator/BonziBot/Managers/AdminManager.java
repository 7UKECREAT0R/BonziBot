package com.lukecreator.BonziBot.Managers;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

/*
 * Very basic class for handling BonziBot administrators.
 */
public class AdminManager {
	
	long[] admins = new long[] {
		415316679610859520l, // Giraffey
		214183045278728202l, // Luke
		206395494648381443l  // Zipdip
	};
	
	public boolean getIsAdmin(long id) {
		for(long admin: admins) {
			if(admin == id)
				return true;
		}
		return false;
	}
	public boolean getIsAdmin(User u) {
		return getIsAdmin(u.getIdLong());
	}
	public boolean getIsAdmin(Member m) {
		return getIsAdmin(m.getUser().getIdLong());
	}
}