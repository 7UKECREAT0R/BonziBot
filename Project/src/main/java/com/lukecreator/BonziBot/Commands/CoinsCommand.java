package com.lukecreator.BonziBot.Commands;

import java.awt.Color;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.UserArg;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Managers.UserAccountManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

public class CoinsCommand extends Command {
	
	public CoinsCommand() {
		this.subCategory = 0;
		this.name = "Coins";
		this.unicodeIcon = "🟡";
		this.description = "See how many coins you or someone else has.";
		this.args = new CommandArgCollection(new UserArg("target").optional());
		this.category = CommandCategory.COINS;
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		User target = e.args.argSpecified("target") ?
			e.args.getUser("target") : e.executor;
		
		UserAccountManager uam = e.bonzi.accounts;
		UserAccount acc = uam.getUserAccount(target);
		
		String title = target.getName();
		
		long coins = acc.getCoins();
		String prefix = "";
		if(coins < 10)
			prefix = "🏚️ Poor Boi";
		else if(coins < 100)
			prefix = "🏠 Moving Up";
		else if(coins < 1000)
			prefix = "⚾ Slightly Balling";
		else if(coins < 5000)
			prefix = "⛓️ Gold Chains";
		else if(coins < 10000)
			prefix = "🏀 Ballin";
		else if(coins < 100000)
			prefix = "🤯 Super Ballin";
		else if(coins < 1000000)
			prefix = "☄️ ULTRA BALLER";
		else prefix = "👑 TRANSCENDENT";
		
		String sCoins = BonziUtils.comma(acc.getCoins()) + " coins";
		
		EmbedBuilder eb = BonziUtils
			.quickEmbed(title, "")
			.setColor(Color.yellow)
			.addField(prefix, sCoins, false);
		
		if(e.isSlashCommand)
			e.slashCommand.replyEmbeds(eb.build()).queue();
		else
			e.channel.sendMessage(eb.build()).queue();
	}
	
}
