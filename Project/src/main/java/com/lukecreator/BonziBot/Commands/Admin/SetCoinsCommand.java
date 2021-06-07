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

public class SetCoinsCommand extends Command {

	public SetCoinsCommand() {
		this.subCategory = 1;
		this.name = "Set Coins";
		this.unicodeIcon = "🟡";
		this.description = "Set your current coins amount.";
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
		long before = ua.getCoins();
		ua.setCoins(amount);
		uam.setUserAccount(target, ua);
		e.channel.sendMessage("completed. " + target.getName() + "'s balance: " + before + " -> " + amount).queue();
		return;
	}
}