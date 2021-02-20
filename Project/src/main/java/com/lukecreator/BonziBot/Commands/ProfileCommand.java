package com.lukecreator.BonziBot.Commands;

import java.awt.Color;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.UserArg;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Managers.UserAccountManager;

import net.dv8tion.jda.api.entities.User;

public class ProfileCommand extends Command {
	
	public ProfileCommand() {
		this.subCategory = 0;
		this.name = "Profile";
		this.unicodeIcon = "📔";
		this.description = "View yours or someone else's profile. Completely customizable!";
		this.args = new CommandArgCollection(new UserArg("target").optional());
		this.category = CommandCategory.FUN;
	}

	@Override
	public void executeCommand(CommandExecutionInfo e) {
		
		boolean specified = e.args.argSpecified("target");
		User target = specified ? e.args.getUser("target") : e.executor;
		UserAccountManager uam = e.bonzi.accounts;
		UserAccount account = uam.getUserAccount(target);
		
		Color favColor = account.favoriteColor;
	}
}