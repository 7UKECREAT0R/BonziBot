package com.lukecreator.BonziBot.Legacy;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.lukecreator.BonziBot.App.PremiumCommand;
import com.lukecreator.BonziBot.UserProfile;
import com.lukecreator.BonziBot.Warn;
import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.ModernWarn;
import com.lukecreator.BonziBot.Data.PremiumItem;
import com.lukecreator.BonziBot.Data.UserAccount;

/**
 * Load legacy data files into the new structure.
 */
public class UserAccountLegacyLoader {
	
	/**
	 * Quick and dirty code for loading and converting legacy files.
	 */
	@SuppressWarnings("unchecked")
	public static HashMap<Long, UserAccount> execute() {
		HashMap<Long, UserAccount> accts = new HashMap<Long, UserAccount>();
		
		// Load all possible information.
		HashMap<Long, Integer> xp = (HashMap<Long, Integer>) DataSerializer.retrieveObject("xp");
		HashMap<Long, Integer> coins = (HashMap<Long, Integer>) DataSerializer.retrieveObject("coins");
		HashMap<Long, HashMap<Long, UserProfile>> profiles = (HashMap<Long, HashMap<Long, UserProfile>>) DataSerializer.retrieveObject("profiles");
		HashMap<Long, List<PremiumCommand>> premiumAccts = (HashMap<Long, List<PremiumCommand>>) DataSerializer.retrieveObject("accounts");
		
		// Put into the accts hashmap.
		for(Entry<Long, Integer> x: xp.entrySet()) {
			if(!accts.containsKey(x.getKey()))
				accts.put(x.getKey(), new UserAccount());
			UserAccount acc = accts.get(x.getKey());
			acc.setXP(x.getValue());
			accts.put(x.getKey(), acc);
		}
		for(Entry<Long, Integer> c: coins.entrySet()) {
			if(!accts.containsKey(c.getKey()))
				accts.put(c.getKey(), new UserAccount());
			UserAccount acc = accts.get(c.getKey());
			acc.setCoins(c.getValue());
			accts.put(c.getKey(), acc);
		}
		for(Entry<Long, HashMap<Long, UserProfile>> _p: profiles.entrySet()) {
			long guildId = _p.getKey();
			for(Entry<Long, UserProfile> prof: _p.getValue().entrySet()) {
				long uid = prof.getKey();
				UserProfile p = prof.getValue();
				
				if(!accts.containsKey(uid))
					accts.put(uid, new UserAccount());
				UserAccount acc = accts.get(uid);
				for(Warn w: p.warns)
					acc.addWarn(new ModernWarn(w, guildId));
				accts.put(uid, acc);
			}
		}
		for(Entry<Long, List<PremiumCommand>> p: premiumAccts.entrySet()) {
			if(!accts.containsKey(p.getKey()))
				accts.put(p.getKey(), new UserAccount());
			UserAccount acc = accts.get(p.getKey());
			for(PremiumCommand pcm: p.getValue()) {
				if(pcm == PremiumCommand.Premium)
					acc.isPremium = true;
				else
					acc.items.add(PremiumItem.values()[pcm.ordinal()]);
			}
			accts.put(p.getKey(), acc);
		}
		
		return accts;
	}
}
