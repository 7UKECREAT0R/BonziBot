package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.ModernWarn;
import com.lukecreator.BonziBot.Data.UserAccount;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

public class DisplayUserInfoCommand extends Command {
	
	public DisplayUserInfoCommand() {
		this.name = "selfinfo";
		this.description = "shows self info";
		this.category = CommandCategory._HIDDEN;
		this.adminOnly = true;
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		UserAccount self = e.bonzi.accounts
			.getUserAccount(e.executor.getIdLong());
		User u = e.executor;
		EmbedBuilder eb = BonziUtils.quickEmbed(u.getName(), u.getId(), u);
		eb.addField("coins", self.getCoins() + "", false);
		eb.addField("xp", self.getXP() + "", false);
		eb.addField("premium", self.isPremium + "", false);
		ModernWarn[] warns = self.getGlobalWarns();
		if(warns.length > 0)
			eb.addField("warn 0:", warns[0].toString(), false);
		e.channel.sendMessageEmbeds(eb.build()).queue();
	}
}
