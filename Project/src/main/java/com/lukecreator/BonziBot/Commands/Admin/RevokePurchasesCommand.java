package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.UserArg;
import com.lukecreator.BonziBot.Data.PremiumItem;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Managers.UserAccountManager;

import net.dv8tion.jda.api.entities.User;

public class RevokePurchasesCommand extends Command {

	public RevokePurchasesCommand() {
		this.subCategory = 1; // moderation commands for admin-level
		this.name = "Revoke Purchases";
		this.unicodeIcon = "💸";
		this.description = "Revoke all of a user's purchases and restore the coins to them.";
		this.args = new CommandArgCollection(new UserArg("target").optional());
		this.category = CommandCategory._HIDDEN;
		this.adminOnly = true;
	}

	@Override
	public void run(CommandExecutionInfo e) {
		boolean self = !e.args.argSpecified("target");
		User target = self ? e.executor : e.args.getUser("target");
		
		int addCoins = 0;
		int premiumPrice = PremiumItem.getPremiumPrice();
		
		UserAccountManager uam = e.bonzi.accounts;
		UserAccount account = uam.getUserAccount(target);
		
		if(account.isPremium)
			addCoins += premiumPrice;
		for(PremiumItem item: account.items)
			addCoins += item.price;
		
		account.isPremium = false;
		account.items.clear();
		account.addCoins(addCoins);
		
		uam.setUserAccount(target, account);
		
		// Send confirmation message.
		e.channel.sendMessage("Revoked " + target.getName() + "'s commands/premium and restored " + addCoins + " coins.").queue();
	}
}