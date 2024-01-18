package com.lukecreator.BonziBot.Managers;

import java.io.EOFException;
import java.util.HashMap;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.InternalLogger;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.IStorableData;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Handling.DefaultMessageHandler;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

/**
 * Handles all of the user accounts.
 */
public class UserAccountManager implements IStorableData {
	
	HashMap<Long, UserAccount> accounts;
	
	public UserAccountManager() {
		this.accounts = new HashMap<Long, UserAccount>();
	}
	/**
	 * Returns if the user levelled up.
	 * @param info
	 */
	public void incrementXp(CommandExecutionInfo info) {
		UserAccount account = this.getUserAccount(info.executor.getIdLong());
		
		if(account.incrementXP()) {
			int level = account.calculateLevel();
			int coins = level * 10 + 10;
			coins += BonziUtils.randomInt(coins);
			account.addCoins(coins);
			
			String userName = info.executor.getName();
			String title;
			
			if(level < 101) {
				if(level > 94)
					userName = userName.toUpperCase();
				title = DefaultMessageHandler
					.LEVEL_UPS[level - 1]
					.replace("{user}", userName);
			} else {
				title = userName + " is now level " + level + "!";
			}
			
			MessageEmbed me = BonziUtils.quickEmbed(title, "`+" + coins + " coins!`")
				.setColor(info.member.getColor()).build();
			info.channel.sendMessageEmbeds(me).queue();
		}
	}
	public UserAccount getUserAccount(User u) {
		return this.getUserAccount(u.getIdLong());
	}
	public void setUserAccount(User u, UserAccount acct) {
		this.setUserAccount(u.getIdLong(), acct);
	}
	public UserAccount getUserAccount(long id) {
		if(!this.accounts.containsKey(id))
			this.accounts.put(id, new UserAccount());
		return this.accounts.get(id);
	}
	public void setUserAccount(long id, UserAccount acct) {
		this.accounts.put(id, acct);
	}
	public HashMap<Long, UserAccount> getAccounts() {
		return this.accounts;
	}
	
	// Data
	@Override
	public void saveData() {
		DataSerializer.writeObject(this.accounts, "modernaccounts");
	}
	@SuppressWarnings("unchecked")
	@Override
	public void loadData() throws EOFException {
		Object o = DataSerializer.retrieveObject("modernaccounts");
		if(o != null) {
			this.accounts = (HashMap<Long, UserAccount>) o;
		}
	}
	
}
