package com.lukecreator.BonziBot.Managers;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

/**
 * Very basic class for handling BonziBot administrators.
 */
public class SpecialPeopleManager {
	
	final long[] admins = new long[] {
            415316679610859520L, // Giraffey
            214183045278728202L, // Luke
            206395494648381443L  // Zipdip
	};
	final long[] bros = new long[] {
            239526347972673537L, // Yucky
            429795795404062720L, // Salty
            348136128932610058L, // Checken
            624752943127592976L, // HypixL
            401913626195263509L, // Zeni
            798235629145292842L, // Onyx2D
            473375262973624333L, // Plasma
            258702715943780352L, // Curoa
            302881384400289792L, // Goldy
            633411509560541245L, // Fez
            288388543535906816L, // 455
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