package com.lukecreator.BonziBot.Data;

import java.awt.Color;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.TimeSpan;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

/**
 * Owned commands, coins, warns, etc...
 */
public class UserAccount implements Serializable {
	
	private static final long serialVersionUID = 2l;
	private static final String NO_BIO = "No bio set yet!";
	public static final int MAX_BIO_LEN = 500;
	
	private long coins = 0;
	private int xp = 0;
	
	private List<ModernWarn> warns = new ArrayList<ModernWarn>();
	
	public boolean isPremium = false;
	public List<PremiumItem> items = new ArrayList<PremiumItem>();
	
	public String bio = NO_BIO;
	public Color favoriteColor = BonziUtils.COLOR_BONZI_PURPLE; // heck yeah
	public String backgroundImage = null;
	public TimeZone timeZone = null;
	public LocalDate birthday = null;
	public AfkData afkData = new AfkData();
	private List<MessageReference> pins = new ArrayList<MessageReference>();
	private final List<Achievement> achievements = new ArrayList<Achievement>();
	private final List<Badge> badges = new ArrayList<Badge>();
	
	public static final long REP_DELAY = 24 * 60 * 60 * 1000;
	private int reputation; // Given by other users every 24 hours.
	private long nextAllowedRep = 0L; // Time when this user can next rep a profile.
	
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
	
	public static long MAX_COINS = 1000000000000000000L;
	public long getCoins() {
		return this.coins;
	}
	public void addCoins(long amount) {
		this.coins += amount;
		if(this.coins > MAX_COINS)
			this.coins = MAX_COINS;
	}
	public void subCoins(long amount) {
		this.coins -= amount;
		if(this.coins < 0)
			this.coins = 0;
	}
	public void setCoins(long coins) {
		this.coins = coins;
		if(this.coins > MAX_COINS)
			this.coins = MAX_COINS;
	}
	
	public int getXP() {
		return this.xp;
	}
	public int calculateLevel() {
		return BonziUtils.calculateLevel(this.xp);
	}
	public boolean incrementXP() {
		int l = calculateLevel();
		this.xp++;
		return calculateLevel() != l;
	}
	public boolean incrementXP(int amount) {
		int l = calculateLevel();
		this.xp += amount;
		return calculateLevel() != l;
	}
	public void setXP(int xp) {
		this.xp = xp;
	}
	
	public ModernWarn[] getWarns(Guild g) {
		return this.getWarns(g.getIdLong());
	}
	public ModernWarn[] getWarns(long guildId) {
		return this.warns
			.stream()
			.filter(w -> w.acquiredGuild == guildId)
			.toArray(ModernWarn[]::new);
	}
	public ModernWarn[] getGlobalWarns() {
		return (ModernWarn[])this.warns.toArray
			(new ModernWarn[warns.size()]);
	}
	public void setWarns(long guildId, List<ModernWarn> warns) {
		for(int i = this.warns.size() - 1; i >= 0; i--) {
			ModernWarn warn = this.warns.get(i);
			if(warn.acquiredGuild == guildId)
				this.warns.remove(i);
		}
		this.warns.addAll(warns);
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
	
	public void addPersonalPin(Message msg, boolean excludeContent) {
		if(this.pins == null)
			this.pins = new ArrayList<MessageReference>();
		this.pins.add(0, new MessageReference(msg, excludeContent));
	}
	public void removePersonalPin(int index) {
		if(this.pins == null)
			return;
		this.pins.remove(index);
	}
	public MessageReference[] getPersonalPinsArray() {
		if(this.pins == null)
			return new MessageReference[0];
		return (MessageReference[])pins.toArray(new MessageReference[pins.size()]);
	}
	public List<MessageReference> getPersonalPins() {
		if(this.pins == null)
			this.pins = new ArrayList<MessageReference>();
		return this.pins;
	}
	public void setPersonalPins(List<MessageReference> apply) {
		this.pins = apply;
		return;
	}
	
	public boolean hasAchievement(Achievement a) {
		return achievements.contains(a);
	}
	public boolean hasBadge(Badge badge) {
		return badges.contains(badge);
	}
	public void awardAchievement(Achievement a) {
		if(!achievements.contains(a))
			achievements.add(a);
	}
	public void awardBadge(Badge badge) {
		if(!badges.contains(badge))
			badges.add(badge);
	}
	public void clearBadges() {
		badges.clear();
	}
	public List<Badge> getBadges() {
		return this.badges;
	}
	public List<Achievement> getAchievements() {
		return this.achievements;
	}
	
	public int getRep() {
		return this.reputation;
	}
	public int addRep() {
		return ++this.reputation;
	}
	public int subRep() {
		return --this.reputation;
	}
	public boolean canRepSomeone() {
		return this.canRepSomeone(System.currentTimeMillis());
	}
	public boolean canRepSomeone(long time) {
		return time > this.nextAllowedRep;
	}
	public TimeSpan timeUntilCanRep() {
		return this.timeUntilCanRep(System.currentTimeMillis());
	}
	public TimeSpan timeUntilCanRep(long time) {
		if(time > this.nextAllowedRep) {
			return TimeSpan.fromMillis(time - this.nextAllowedRep);
		} else return TimeSpan.ZERO;
	}
	public void setRepDelay() {
		long now = System.currentTimeMillis();
		this.nextAllowedRep = now + REP_DELAY;
	}
	public void setRep(int rep) {
		this.reputation = rep;
	}
	
	public boolean hasBirthday() {
		return this.birthday != null;
	}
	public LocalDate getBirthday() {
		return this.birthday;
	}
	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}
	public boolean isTodayBirthday() {
		if(this.birthday == null)
			return false;
		LocalDate now = LocalDate.now();
		this.birthday = this.birthday.withYear(now.getYear());
		Month aMonth = this.birthday.getMonth();
		Month bMonth = now.getMonth();
		int aDay = this.birthday.getDayOfMonth();
		int bDay = now.getDayOfMonth();
		return aDay == bDay && aMonth == bMonth;
	}
	public int daysUntilBirthday() {
		if(this.birthday == null)
			return -1;
		
		LocalDate now = LocalDate.now();
		this.birthday = this.birthday.withYear(now.getYear());
		int dayOfYear = this.birthday.getDayOfYear();
		int yearLength = now.lengthOfYear();
		int currentDay = now.getDayOfYear();
		
		if(currentDay == dayOfYear)
			return 0;
		else if(currentDay < dayOfYear)
			return dayOfYear - currentDay;
		else
			return yearLength - currentDay + dayOfYear;
	}
	public void disableBirthday() {
		this.birthday = null;
	}
}