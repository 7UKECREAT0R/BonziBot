package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Managers.UserAccountManager;

public class BioCommand extends Command {
	
	public BioCommand() {
		this.subCategory = 3;
		this.name = "Bio";
		this.unicodeIcon = "✏️";
		this.description = "A short description of yourself that will show on your BonziBot profile!";
		this.args = CommandArgCollection.single("bio");
		this.category = CommandCategory.FUN;
	}

	@Override
	public void executeCommand(CommandExecutionInfo e) {
		
		String specified = e.args.getString("bio");
		
		if(specified.length() < 1) {
			e.channel.sendMessage(BonziUtils.failureEmbed("you gotta type a bio")).queue();
			return;
		}
		if(specified.length() > UserAccount.MAX_BIO_LEN) {
			e.channel.sendMessage(BonziUtils.failureEmbed("Your bio can be " + UserAccount.MAX_BIO_LEN + 
					" characters max! The one you sent is " + specified.length() + ".")).queue();
			return;
		}
		
		UserAccountManager uam = e.bonzi.accounts;
		UserAccount acc = uam.getUserAccount(e.executor);
		acc.bio = specified.replace('\n', ' ');
		uam.setUserAccount(e.executor, acc);
		
		String prefix = BonziUtils.getPrefixOrDefault(e);
		e.channel.sendMessage(BonziUtils.successEmbed("Your bio is now set!",
			"Run `" + prefix + "profile` to see it!")).queue();
		return;
	}
}