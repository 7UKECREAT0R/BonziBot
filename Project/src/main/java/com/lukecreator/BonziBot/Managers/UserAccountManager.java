package com.lukecreator.BonziBot.Managers;

import java.util.HashMap;

import com.lukecreator.BonziBot.InternalLogger;
import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.IStorableData;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Legacy.UserAccountLegacyLoader;

/*
 * Handles all of the user accounts.
 */
public class UserAccountManager implements IStorableData {
	
	HashMap<Long, UserAccount> accounts;
	
	public UserAccountManager() {
		accounts = new HashMap<Long, UserAccount>();
	}
	public UserAccount getUserAccount(long id) {
		if(!accounts.containsKey(id))
			accounts.put(id, new UserAccount());
		return accounts.get(id);
	}
	public void setUserAccount(long id, UserAccount acct) {
		accounts.put(id, acct);
	}
	
	// Data
	public void loadLegacy() {
		accounts = UserAccountLegacyLoader.execute();
		InternalLogger.print("Loaded legacy user accounts.");
	}
	@Override
	public void saveData() {
		DataSerializer.writeObject(accounts, "modernaccounts");
	}
	@SuppressWarnings("unchecked")
	@Override
	public void loadData() {
		Object o = DataSerializer.retrieveObject("modernaccounts");
		if(o != null) {
			accounts = (HashMap<Long, UserAccount>) o;
		}
	}
	
}
