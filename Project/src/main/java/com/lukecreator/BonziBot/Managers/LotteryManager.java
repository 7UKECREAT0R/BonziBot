package com.lukecreator.BonziBot.Managers;

import java.io.EOFException;
import java.util.Random;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Tuple;
import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.IStorableData;
import com.lukecreator.BonziBot.Data.UserAccount;

import net.dv8tion.jda.api.entities.User;

public class LotteryManager implements IStorableData {
	
	public static final String FILE = "lottery";
	public static final int TICKET_COST = 10;
	public static final int RESET_TO = 1000;
	public static final int WIN_CHANCE = 10000;
	public static final String S_TICKET_COST = BonziUtils.comma(TICKET_COST);
	public static final String S_RESET_TO = BonziUtils.comma(RESET_TO);
	public static final String S_WIN_CHANCE = BonziUtils.comma(WIN_CHANCE);
	
	Random rand = new Random();
	int lottery = RESET_TO;
	
	public int getLottery() {
		return lottery;
	}
	public void incrementLottery() {
		
	}
	/*
	 * Buy a lottery ticket for this user. Automatically
	 * sets their coins, and returns if they won or not.
	 * Also returns the winnings or losses. (pos or neg)
	 */
	public Tuple<Boolean, Integer> doLottery(User buyer, BonziBot bonzi) {
		UserAccount ua = bonzi.accounts.getUserAccount(buyer);
		int coins = ua.getCoins();
		
		boolean win = rand.nextInt(WIN_CHANCE) == 0;
		int winnings = win ? lottery : -10;
		coins += winnings;
		
		if(win)
			lottery = RESET_TO;
		else
			lottery += TICKET_COST;
		
		ua.setCoins(coins);
		bonzi.accounts.setUserAccount(buyer, ua);
		
		return new Tuple<Boolean, Integer>(win, winnings);
	}
	
	@Override
	public void saveData() {
		DataSerializer.writeObject(lottery, FILE);
	}
	@Override
	public void loadData() throws EOFException {
		Object o = DataSerializer.retrieveObject(FILE);
		if(o != null) lottery = (int)(Integer)o;
	}
}
