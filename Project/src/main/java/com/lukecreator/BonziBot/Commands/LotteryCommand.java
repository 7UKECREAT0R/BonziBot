package com.lukecreator.BonziBot.Commands;

import java.awt.Color;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Tuple;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Managers.LotteryManager;
import com.lukecreator.BonziBot.Managers.UserAccountManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class LotteryCommand extends Command {

	public LotteryCommand() {
		this.subCategory = 1;
		this.name = "Lottery";
		this.unicodeIcon = "🎟️";
		this.description = "Pay " + LotteryManager.S_TICKET_COST + " coins and have a 1/" + LotteryManager.S_WIN_CHANCE + " chance to win the whole lot!";
		this.category = CommandCategory.COINS;
	}

	@Override
	public void run(CommandExecutionInfo e) {
		
		User u = e.executor;
		UserAccountManager uam = e.bonzi.accounts;
		UserAccount account = uam.getUserAccount(u);
		long currentCoins = account.getCoins();
		
		if(currentCoins < LotteryManager.TICKET_COST) {
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(BonziUtils.failureEmbed("You need " + LotteryManager.S_TICKET_COST + 
					" coins to buy a lottery ticket!", "You currently have " + BonziUtils.comma(currentCoins) + " coins.")).queue();
			else
				e.channel.sendMessageEmbeds(BonziUtils.failureEmbed("You need " + LotteryManager.S_TICKET_COST + 
					" coins to buy a lottery ticket!", "You currently have " + BonziUtils.comma(currentCoins) + " coins.")).queue();
			return;
		}
		
		Tuple<Boolean, Integer> output = 
			e.bonzi.lottery.doLottery(u, e.bonzi);
		boolean win = output.getA();
		int winnings = output.getB();
		String oldCoins = BonziUtils.comma(currentCoins);
		currentCoins += winnings;
		String newCoins = BonziUtils.comma(currentCoins);
		
		if(win) {
			String winningsString = BonziUtils.comma(winnings);
			EmbedBuilder eb = BonziUtils.quickEmbed("💰 IT'S A WINNER! 💰", "You just won " +
				winningsString + " coins!\nYou went from " + oldCoins + " to " + newCoins + " coins!", Color.yellow);
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(eb.build()).queue();
			else
				e.channel.sendMessageEmbeds(eb.build()).queue();
			return;
		} else {
			MessageEmbed me = BonziUtils.failureEmbed("You didn't win this time...  -" + LotteryManager.S_TICKET_COST + " coins.",
					"The lottery now has " + BonziUtils.comma(e.bonzi.lottery.getLottery()) + " coins in it!");
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(me).queue();
			else
				e.channel.sendMessageEmbeds(me).queue();
			return;
		}
	}
}