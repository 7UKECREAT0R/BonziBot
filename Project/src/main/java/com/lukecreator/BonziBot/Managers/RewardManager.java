package com.lukecreator.BonziBot.Managers;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.IStorableData;
import com.lukecreator.BonziBot.Data.UserAccount;

import net.dv8tion.jda.api.entities.User;

/**
 * Manages daily rewards and streaks.
 */
public class RewardManager implements IStorableData {
	
	public static final long ONE_HOUR = 3600000;
	public static final long ONE_DAY = ONE_HOUR * 24;
	public static final int BASE_COINS = 100;
	public static final int STREAK_REWARDS = 25;
	public static final String FILE_TIMES = "reward_timestamps";
	public static final String FILE_STREAKS = "reward_streaks";
	
	// User ID, Timestamp
	// User ID, Streak
	HashMap<Long, Long> lastCollection = new HashMap<Long, Long>();
	HashMap<Long, Integer> currentStreak = new HashMap<Long, Integer>();

	public void setStreak(User u, int streak) { this.setStreak(u.getIdLong(), streak); }
	public void setStreak(long l, int streak) {
		this.currentStreak.put(l, streak);
	}
	public int getStreak(User u) {
		return this.getStreak(u.getIdLong());
	}
	public int getStreak(long l) {
        return this.currentStreak.getOrDefault(l, 0);
	}
	public long getLastCollection(User u) {
		return this.getLastCollection(u.getIdLong());
	}
	public long getLastCollection(long id) {
        return this.lastCollection.getOrDefault(id, 0L);
	}
	public long timeUntilCanClaim(User u) {
		return this.timeUntilCanClaim(u.getIdLong());
	}
	public long timeUntilCanClaim(long id) {
		long now = System.currentTimeMillis();
		long last = this.getLastCollection(id);
		long nextClaim = last + (ONE_DAY - ONE_HOUR);
		return nextClaim - now;
	}
	public boolean canClaim(User u) {
		return this.canClaim(u.getIdLong());
	}
	public boolean canClaim(long id) {
		return this.timeUntilCanClaim(id) <= 0;
	}

	/**
	 *   Claim a daily reward as a user.
	 * Returns the amount of coins earned.
	 */
	public int claimAs(User u, BonziBot bb) {
		return this.claimAs(u.getIdLong(), bb);
	}
	/**
	 *   Claim a daily reward as a user.
	 * Returns the amount of coins earned.
	 */
	public int claimAs(long id, BonziBot bb) {
		
		long timeLeft = this.timeUntilCanClaim(id);
		
		int streak = this.getStreak(id);
		if(timeLeft <= -ONE_DAY * 2)
			streak = 0;
		
		int bonus = streak * streak; // Holy cow power of 2??????
		int receive = BASE_COINS + bonus;
		
		UserAccount acc = bb.accounts.getUserAccount(id);
		acc.addCoins(receive);
		bb.accounts.setUserAccount(id, acc);
		
		streak++;
		this.currentStreak.put(id, streak);
		this.lastCollection.put(id, System.currentTimeMillis());
		
		return receive;
	}
	public void setLastCollectionTime(long id, long time) {
		this.lastCollection.put(id, time);
	}
	
	/**
	 * Get the top users.
     */
	public List<Entry<Long, Integer>> getTop() {
		Set<Entry<Long, Integer>> set = this.currentStreak.entrySet();
		List<Entry<Long, Integer>> list = new ArrayList<Entry<Long, Integer>>(set);
		Collections.sort(list, new RewardSort());
		return list;
	}
	public class RewardSort implements Comparator<Entry<Long, Integer>> {
		@Override
		public int compare(Entry<Long, Integer> o1, Entry<Long, Integer> o2) {
			return o2.getValue() - o1.getValue();
		}
	}

	
	
	@Override
	public void saveData() {
		DataSerializer.writeObject(this.lastCollection, FILE_TIMES);
		DataSerializer.writeObject(this.currentStreak, FILE_STREAKS);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void loadData() throws EOFException {
		Object o1 = DataSerializer.retrieveObject(FILE_TIMES),
			o2 = DataSerializer.retrieveObject(FILE_STREAKS);
		
		if(o1 != null)
			this.lastCollection = (HashMap<Long, Long>) o1;
		if(o2 != null)
			this.currentStreak = (HashMap<Long, Integer>) o2;
	}
}