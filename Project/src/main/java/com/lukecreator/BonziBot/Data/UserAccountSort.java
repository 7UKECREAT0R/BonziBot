package com.lukecreator.BonziBot.Data;

import java.util.Comparator;
import java.util.Map;

public class UserAccountSort implements Comparator<Map.Entry<Long, UserAccount>> {
	
	public enum UASType {
		COINS, XP
	}
	
	UASType type;
	
	public UserAccountSort(UASType type) {
		this.type = type;
	}
	
	@Override
	public int compare(Map.Entry<Long, UserAccount> o1, Map.Entry<Long, UserAccount> o2) {
		switch(type) {
		case COINS:
			return o1.getValue().getCoins() - o2.getValue().getCoins();
		case XP:
			return o1.getValue().getXP() - o2.getValue().getXP();
		default: return 0;
		}
	}
}
