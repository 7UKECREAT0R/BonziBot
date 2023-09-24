package com.lukecreator.BonziBot.Commands;

import java.awt.Color;
import java.util.Random;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.IntArg;
import com.lukecreator.BonziBot.Data.Achievement;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Managers.UserAccountManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

public class ChanceCommand extends Command {
	
	Random random = new Random();
	
	public ChanceCommand() {
		this.subCategory = 1;
		this.name = "Chance";
		this.icon = GenericEmoji.fromEmoji("🎲");
		this.description = "You'll have a 50/50 chance to double your input or lose it all!";
		this.args = new CommandArgCollection(new IntArg("amount"));
		this.category = CommandCategory.COINS;
	}
	
	@Override
	public void run(CommandExecutionInfo e) {
		
		User u = e.executor;
		UserAccountManager uam = e.bonzi.accounts;
		UserAccount account = uam.getUserAccount(u);
		
		int amount = e.args.getInt("amount");
		long balance = account.getCoins();
		String amountString = BonziUtils.comma(amount);
		String balanceString = BonziUtils.comma(balance);
		
		if(amount < 0) {
			EmbedBuilder eb = BonziUtils.quickEmbed(
				"💸 YOU WON 💸", amountString + " COINS!\n"
				+ "Current balance: " + BonziUtils.comma(balance + amount), Color.yellow);
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(eb.build()).queue();
			else
				e.channel.sendMessageEmbeds(eb.build()).queue();
			return;
		} else if(amount == 0) {
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(BonziUtils.successEmbed("🥳 CONGRATULATIONS! You won a 1000x coins multiplier! (+0 COINS!) 🥳")).queue();
			else
				e.channel.sendMessageEmbeds(BonziUtils.successEmbed("🥳 CONGRATULATIONS! You won a 1000x coins multiplier! (+0 COINS!) 🥳")).queue();
			return;
		}
		
		if(amount > balance) {
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(BonziUtils.failureEmbedIncomplete
					("You can't afford that chance bro!").setDescription
					("Current balance: " + balanceString).build()).queue();
			else
				e.channel.sendMessageEmbeds(BonziUtils.failureEmbedIncomplete
					("You can't afford that chance bro!").setDescription
					("Current balance: " + balanceString).build()).queue();
			return;
		}
		
		boolean win = this.random.nextBoolean();
		
		long newCoins = balance;
		if(win) {
			newCoins += amount;
			
			if(amount >= 1000)
				BonziUtils.tryAwardAchievement(e.channel, e.bonzi, e.executor, Achievement.LUCKY);
			if(amount >= 10000)
				BonziUtils.tryAwardAchievement(e.channel, e.bonzi, e.executor, Achievement.LUCK_MASTER);
			
			EmbedBuilder eb = BonziUtils.quickEmbed(
				"💸 YOU WON 💸", "+" + amountString + " COINS!\n"
				+ "Current balance: " + BonziUtils.comma(newCoins), Color.yellow);
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(eb.build()).queue();
			else
				e.channel.sendMessageEmbeds(eb.build()).queue();
		} else {
			newCoins -= amount;
			EmbedBuilder eb = BonziUtils.quickEmbed(
				"❌ YOU LOST... ❌", "-" + amountString + " COINS\n"
				+ "Current balance: " + BonziUtils.comma(newCoins), Color.red);
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(eb.build()).queue();
			else
				e.channel.sendMessageEmbeds(eb.build()).queue();
		}
		account.setCoins(newCoins);
		uam.setUserAccount(u, account);
	}
}
