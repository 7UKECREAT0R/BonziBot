package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.ACommand;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.UserAccount;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

public class DisplayUserInfoCommand extends ACommand {
	
	public DisplayUserInfoCommand() {
		this.name = "selfinfo";
		this.description = "shows self info";
		this.usage = "selfinfo";
		this.category = CommandCategory._TOPLEVEL;
		
		this.usesArgs = false;
		this.adminOnly = true;
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		UserAccount self = e.bonzi.accounts
			.getUserAccount(e.executor.getIdLong());
		User u = e.executor;
		EmbedBuilder eb = BonziUtils.quickEmbed(u.getName(), u.getId(), u);
		eb.addField("coins", self.coins + "", false);
		eb.addField("xp", self.xp + "", false);
		eb.addField("premium", self.isPremium + "", false);
		if(!self.warns.isEmpty())
			eb.addField("warn 0:", self.warns.get(0).toString(), false);
		e.channel.sendMessage(eb.build()).queue();
	}
}
