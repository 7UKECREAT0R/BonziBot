package com.lukecreator.BonziBot.Data;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import com.lukecreator.BonziBot.BonziUtils;

import net.dv8tion.jda.api.entities.Invite.Guild;

/**
 * Owned commands, coins, warns, etc...
 */
public class UserAccount implements Serializable {
	
	private static final long serialVersionUID = 2l;
	private static final String NO_BIO = "No bio set yet!";
	
	private int coins = 0;
	private int xp = 0;
	
	private List<ModernWarn> warns = new ArrayList<ModernWarn>();
	
	public boolean isPremium = false;
	public List<PremiumItem> items = new ArrayList<PremiumItem>();
	
	public String bio = NO_BIO;
	public Color favoriteColor = BonziUtils.COLOR_BONZI_PURPLE; // heck yeah
	public String backgroundImage = null;
	public TimeZone timeZone = null;
	public final List<Achievement> achievements = new ArrayList<Achievement>();
	public final List<Badge> badges = new ArrayList<Badge>();
	
	// Opting stuff. Required for a lot of botlists.
	public boolean optOutDms = false;
	public boolean optOutExpose = false;
	
	public boolean hasItem(PremiumItem item) {
		if(isPremium) return true;
		if(items.isEmpty()) return false;
		for(PremiumItem i: items)
			if(item == i)
				return true;
		return false;
	}
	public PremiumItem[] getOwnedItems() {
		PremiumItem[] all = PremiumItem.values();
		int validCount = 0;
		for(PremiumItem i: all)
			if(i.enabled) validCount++;
		
		if(this.isPremium) {
			int atIndex = 0;
			PremiumItem[] items = new PremiumItem[validCount];
			for(int i = 0; i < all.length; i++) {
				PremiumItem item = all[i];
				if(item.enabled)
					items[atIndex++] = item;
			}
			return items;
		} else {
			return (PremiumItem[]) items.toArray(new PremiumItem[items.size()]);
		}
	}
	
	public int getCoins() {
		return this.coins;
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
	
	public int getXP() {
		return this.xp;
	}
	public int getLevel() {
		return BonziUtils.calculateLevel(this.xp);
	}
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
	
	public ModernWarn[] getWarns(Guild g) {
		return this.getWarns(g.getIdLong());
	}
	public ModernWarn[] getWarns(long guildId) {
		List<ModernWarn> buffer = new ArrayList<ModernWarn>();
		for(ModernWarn warn: this.warns) {
			if(warn.acquiredGuild == guildId)
				buffer.add(warn);
		}
		if(buffer.size() < 1)
			return new ModernWarn[0];
		
		return (ModernWarn[])buffer.toArray
			(new ModernWarn[buffer.size()]);
	}
	public ModernWarn[] getGlobalWarns() {
		return (ModernWarn[])this.warns.toArray
			(new ModernWarn[warns.size()]);
	}
	public void addWarn(ModernWarn warn) {
		this.warns.add(warn);
	}
	public ModernWarn removeWarnAt(int index) {
		return this.warns.remove(index);
	}
	public ModernWarn removeWarn(long id) {
		for(int i = 0; i < warns.size(); i++) {
			ModernWarn mw = warns.get(i);
			if(mw.id == id)
				return warns.remove(i);
		}
		return null;
	}
}