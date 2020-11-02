package com.lukecreator.BonziBot.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * Owned commands, coins, warns, etc...
 */
public class UserAccount implements Serializable {
	
	private static final long serialVersionUID = 1;
	
	public int coins = 0;
	public int xp = 0;
	
	public List<ModernWarn> warns = new ArrayList<ModernWarn>();
	
	public boolean isPremium = false;
	public List<PremiumItem> items = new ArrayList<PremiumItem>();
	
	
}
