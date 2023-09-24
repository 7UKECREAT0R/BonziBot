package com.lukecreator.BonziBot.Data;

import java.util.Comparator;
import java.util.Map.Entry;

public class CoinSort implements Comparator<Entry<Long, UserAccount>> {
	@Override
	public int compare(Entry<Long, UserAccount> o1, Entry<Long, UserAccount> o2) {
		long diff = (o1.getValue().getCoins() - o2.getValue().getCoins());
		
		if(diff < (long)Integer.MIN_VALUE)
			diff = Integer.MIN_VALUE;
		if(diff > (long)Integer.MAX_VALUE)
			diff = Integer.MAX_VALUE;
		
		return (int)diff;
	}
}
