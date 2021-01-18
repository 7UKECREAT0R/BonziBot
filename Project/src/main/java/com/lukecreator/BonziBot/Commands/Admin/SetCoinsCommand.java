package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.IntArg;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Managers.UserAccountManager;

public class SetCoinsCommand extends Command {

	public SetCoinsCommand() {
		this.subCategory = 1;
		this.name = "Set Coins";
		this.unicodeIcon = "🟡";
		this.description = "Set your current coins amount.";
		this.args = new CommandArgCollection(new IntArg("amount"));
		this.category = CommandCategory._HIDDEN;
		this.adminOnly = true;
	}

	@Override
	public void executeCommand(CommandExecutionInfo e) {
		int amount = e.args.getInt("amount");
		UserAccountManager uam = e.bonzi.accounts;
		UserAccount ua = uam.getUserAccount(e.executor);
		ua.setCoins(amount);
		uam.setUserAccount(e.executor, ua);
		e.channel.sendMessage("completed. new balance: " + ua.getCoins()).queue();
		return;
	}
}