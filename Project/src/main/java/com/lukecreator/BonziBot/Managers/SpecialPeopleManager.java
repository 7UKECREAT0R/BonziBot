package com.lukecreator.BonziBot.Managers;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

/**
 * Very basic class for handling BonziBot administrators.
 */
public class SpecialPeopleManager {
	
	final long[] admins = new long[] {
			415316679610859520l, // Giraffey
			214183045278728202l, // Luke
			206395494648381443l  // Zipdip
	};
	final long[] bros = new long[] {
			239526347972673537l, // Yucky
			537735335300562975l, // Cwaky
			292755668623163399l, // Salter
			348136128932610058l, // Chekn
			624752943127592976l, // Materw
			401913626195263509l, // Zerni
			358068430349271040l,  // Onix
			473375262973624333l, // Palsma
			258702715943780352l, // Cooba
			302881384400289792l, // Goaldy
			633411509560541245l,  // fEZ
			250381056904003585l  // Bist
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
	
	public boolean getIsBro(long id) {
		for(long bro: bros) {
			if(bro == id)
				return true;
		}
		return false;
	}
	public boolean getIsBro(User u) {
		return getIsBro(u.getIdLong());
	}
	public boolean getIsBro(Member m) {
		return getIsBro(m.getUser().getIdLong());
	}
}