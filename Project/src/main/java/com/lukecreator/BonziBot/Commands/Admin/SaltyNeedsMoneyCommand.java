package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Managers.UserAccountManager;

public class SaltyNeedsMoneyCommand extends Command {

	static final long SALTE = 292755668623163399l;
	
	public SaltyNeedsMoneyCommand() {
		this.subCategory = 0;
		this.name = "SaltyNeedsMoney";
		this.unicodeIcon = "🤑";
		this.description = "he need it";
		this.args = null;
		this.category = CommandCategory._HIDDEN;
	}

	@Override
	public void executeCommand(CommandExecutionInfo e) {
		UserAccountManager uam = e.bonzi.accounts;
		
		UserAccount account = uam.getUserAccount(SALTE);
		account.addCoins(10);
		uam.setUserAccount(SALTE, account);
		
		e.channel.sendMessage("he gotm the ten coines").queue();
	}
}