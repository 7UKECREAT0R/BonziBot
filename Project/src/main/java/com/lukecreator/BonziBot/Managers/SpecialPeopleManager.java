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
			429795795404062720l, // Salty
			348136128932610058l, // Checken
			624752943127592976l, // HypixL
			401913626195263509l, // Zeni
			798235629145292842l, // Onyx2D
			473375262973624333l, // Plasma
			258702715943780352l, // Curoa
			302881384400289792l, // Goldy
			633411509560541245l, // Fez
			288388543535906816l, // 455
	};
	
	public boolean getIsAdmin(long id) {
		for(long admin: this.admins) {
			if(admin == id)
				return true;
		}
		return false;
	}
	public boolean getIsAdmin(User u) {
		return this.getIsAdmin(u.getIdLong());
	}
	public boolean getIsAdmin(Member m) {
		return this.getIsAdmin(m.getUser().getIdLong());
	}
	public long[] getAdmins() {
		return this.admins;
	}
	
	public boolean getIsBro(long id) {
		for(long bro: this.bros) {
			if(bro == id)
				return true;
		}
		return false;
	}
	public boolean getIsBro(User u) {
		return this.getIsBro(u.getIdLong());
	}
	public boolean getIsBro(Member m) {
		return this.getIsBro(m.getUser().getIdLong());
	}
	public long[] getBros() {
		return this.bros;
	}
}