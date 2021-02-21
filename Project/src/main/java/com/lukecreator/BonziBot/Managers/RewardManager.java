package com.lukecreator.BonziBot.Managers;

import java.io.EOFException;
import java.util.HashMap;

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
	
	public int getStreak(User u) {
		return getStreak(u.getIdLong());
	}
	public int getStreak(long l) {
		if(currentStreak.containsKey(l))
			return currentStreak.get(l);
		else return 0;
	}
	public long getLastCollection(User u) {
		return getLastCollection(u.getIdLong());
	}
	public long getLastCollection(long id) {
		if(lastCollection.containsKey(id))
			return lastCollection.get(id);
		else return 0l;
	}
	public long timeUntilCanClaim(User u) {
		return timeUntilCanClaim(u.getIdLong());
	}
	public long timeUntilCanClaim(long id) {
		long now = System.currentTimeMillis();
		long last = getLastCollection(id);
		long nextClaim = last + (ONE_DAY - ONE_HOUR);
		return nextClaim - now;
	}
	public boolean canClaim(User u) {
		return canClaim(u.getIdLong());
	}
	public boolean canClaim(long id) {
		return timeUntilCanClaim(id) <= 0;
	}

	/**
	 *   Claim a daily reward as a user.
	 * Returns the amount of coins earned.
	 */
	public int claimAs(User u, BonziBot bb) {
		return claimAs(u.getIdLong(), bb);
	}
	/**
	 *   Claim a daily reward as a user.
	 * Returns the amount of coins earned.
	 */
	public int claimAs(long id, BonziBot bb) {
		
		long timeLeft = timeUntilCanClaim(id);
		int streak = getStreak(id);
		if(timeLeft <= -ONE_DAY * 2)
			streak = 0;
		
		int bonus = streak * STREAK_REWARDS;
		int receive = BASE_COINS + bonus;
		
		UserAccount acc = bb.accounts.getUserAccount(id);
		acc.addCoins(receive);
		bb.accounts.setUserAccount(id, acc);
		
		streak++;
		currentStreak.put(id, streak);
		lastCollection.put(id, System.currentTimeMillis());
		
		return receive;
	}
	public void setLastCollectionTime(long id, long time) {
		lastCollection.put(id, time);
	}
	
	@Override
	public void saveData() {
		DataSerializer.writeObject(lastCollection, FILE_TIMES);
		DataSerializer.writeObject(currentStreak, FILE_STREAKS);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void loadData() throws EOFException {
		Object o1 = DataSerializer.retrieveObject(FILE_TIMES),
			o2 = DataSerializer.retrieveObject(FILE_STREAKS);
		
		if(o1 != null)
			lastCollection = (HashMap<Long, Long>) o1;
		if(o2 != null)
			currentStreak = (HashMap<Long, Integer>) o2;
	}
}