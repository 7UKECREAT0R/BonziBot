package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.IntArg;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Managers.UserAccountManager;

public class SubtractCoinsCommand extends Command {

	public SubtractCoinsCommand() {
		this.subCategory = 1;
		this.name = "Subtract Coins";
		this.unicodeIcon = "-🟡";
		this.description = "Remove coins from your account.";
		this.args = new CommandArgCollection(new IntArg("amount"));
		this.category = CommandCategory._HIDDEN;
		this.adminOnly = true;
	}

	@Override
	public void executeCommand(CommandExecutionInfo e) {
		int amount = e.args.getInt("amount");
		UserAccountManager uam = e.bonzi.accounts;
		UserAccount ua = uam.getUserAccount(e.executor);
		ua.subCoins(amount);
		uam.setUserAccount(e.executor, ua);
		e.channel.sendMessage("completed. new balance: " + ua.getCoins()).queue();
		return;
	}
}