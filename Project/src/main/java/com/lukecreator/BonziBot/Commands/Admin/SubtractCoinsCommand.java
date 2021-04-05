package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.IntArg;
import com.lukecreator.BonziBot.CommandAPI.UserArg;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Managers.UserAccountManager;

import net.dv8tion.jda.api.entities.User;

public class SubtractCoinsCommand extends Command {

	public SubtractCoinsCommand() {
		this.subCategory = 1;
		this.name = "Subtract Coins";
		this.unicodeIcon = "-🟡";
		this.description = "Remove coins from your account.";
		this.args = new CommandArgCollection(new IntArg("amount"), new UserArg("target").optional());
		this.category = CommandCategory._HIDDEN;
		this.adminOnly = true;
	}

	@Override
	public void executeCommand(CommandExecutionInfo e) {
		int amount = e.args.getInt("amount");
		User target = e.args.argSpecified("target") ? e.args.getUser("target") : e.executor;
		
		UserAccountManager uam = e.bonzi.accounts;
		UserAccount ua = uam.getUserAccount(target);
		int before = ua.getCoins();
		ua.subCoins(amount);
		int now = ua.getCoins();
		uam.setUserAccount(target, ua);
		e.channel.sendMessage("completed. " + target.getName() + "'s balance: " + before + " -> " + now).queue();
		return;
	}
}