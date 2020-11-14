package com.lukecreator.BonziBot.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.lukecreator.BonziBot.BonziUtils;

/*
 * Owned commands, coins, warns, etc...
 */
public class UserAccount implements Serializable {
	
	private static final long serialVersionUID = 1;
	
	private int coins = 0;
	private int xp = 0;
	
	public List<ModernWarn> warns = new ArrayList<ModernWarn>();
	
	public boolean isPremium = false;
	public List<PremiumItem> items = new ArrayList<PremiumItem>();
	
	// Getters
	public int getCoins() {
		return this.coins;
	}
	public int getXP() {
		return this.xp;
	}
	public int getLevel() {
		return BonziUtils.calculateLevel(this.xp);
	}
	
	// Setters
	/*
	 * Returns if the user leveled up.
	 */
	public boolean incrementXP() {
		return incrementXP(1);
	}
	public boolean incrementXP(int amount) {
		int l = getLevel();
		this.xp += amount;
		return getLevel() != l;
	}
	public void setXP(int xp) {
		this.xp = xp;
	}
	public void addCoins(int amount) {
		this.coins += amount;
	}
	public void subCoins(int amount) {
		this.coins -= amount;
	}
	public void setCoins(int coins) {
		this.coins = coins;
	}
}