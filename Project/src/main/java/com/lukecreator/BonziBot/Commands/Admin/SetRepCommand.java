package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.IntArg;
import com.lukecreator.BonziBot.CommandAPI.UserArg;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Managers.UserAccountManager;

import net.dv8tion.jda.api.entities.User;

public class SetRepCommand extends Command {

	public SetRepCommand() {
		this.subCategory = 0;
		this.name = "Set Rep";
		this.icon = GenericEmoji.fromEmoji("♾️");
		this.description = "set reputation of user";
		this.args = new CommandArgCollection(new UserArg("target"), new IntArg("new rep"));
		this.category = CommandCategory._HIDDEN;
		this.adminOnly = true;
	}

	@Override
	public void run(CommandExecutionInfo e) {
		User target = e.args.getUser("target");
		int newRep = e.args.getInt("new rep");
		
		UserAccountManager uam = e.bonzi.accounts;
		UserAccount account = uam.getUserAccount(target);
		account.setRep(newRep);
		uam.setUserAccount(target, account);
		
		e.channel.sendMessageEmbeds(BonziUtils.successEmbed("Successfully set reputation to " + newRep + ".")).queue();
	}
}