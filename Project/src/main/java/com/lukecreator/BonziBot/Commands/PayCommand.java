package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.IntArg;
import com.lukecreator.BonziBot.CommandAPI.UserArg;
import com.lukecreator.BonziBot.Data.Achievement;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Managers.UserAccountManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class PayCommand extends Command {
	
	public PayCommand() {
		this.subCategory = 0;
		this.name = "Pay";
		this.unicodeIcon = "🎁";
		this.description = "Pay another user with coins!";
		this.args = new CommandArgCollection(
			new UserArg("receiver"),
			new IntArg("amount"));
		this.category = CommandCategory.COINS;
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		User target = e.args.getUser("receiver");
		UserAccountManager uam = e.bonzi.accounts;
		UserAccount receiveAccount = uam.getUserAccount(target);
		UserAccount senderAccount = uam.getUserAccount(e.executor);
		int amount = e.args.getInt("amount");
		
		// Validation
		if(target.isBot()) {
			MessageEmbed msg = BonziUtils.failureEmbed("bro iunno if you want to gift a bot coins...");
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(msg).queue();
			else
				e.channel.sendMessage(msg).queue();
			return;
		}
		if(amount < 0) {
			MessageEmbed msg = BonziUtils.failureEmbed("nice try STEALER");
			BonziUtils.tryAwardAchievement(e.channel, e.bonzi, e.executor, Achievement.THIEF);
			uam.setUserAccount(e.executor, senderAccount);
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(msg).queue();
			else
				e.channel.sendMessage(msg).queue();
			return;
		} else if(amount == 0) {
			MessageEmbed msg = BonziUtils.failureEmbed("you need to put a number that's not 0 lol");
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(msg).queue();
			else
				e.channel.sendMessage(msg).queue();
			return;
		}
		
		// Check account balance.
		long current = senderAccount.getCoins();
		String currentString = BonziUtils.comma(current);
		if(amount > current) {
			MessageEmbed msg = BonziUtils.failureEmbed("You don't have enough in your balance to pay that much! Current amount: " + currentString);
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(msg).queue();
			else
				e.channel.sendMessage(msg).queue();
			return;
		}
		
		senderAccount.subCoins(amount);
		receiveAccount.addCoins(amount);
		
		uam.setUserAccount(e.executor, senderAccount);
		uam.setUserAccount(target, receiveAccount);
		
		EmbedBuilder eb = BonziUtils.successEmbedIncomplete
				("Successfully paid " + target.getName() + " " + BonziUtils.comma(amount) + " " + BonziUtils.plural("coin", amount) + "!");
		eb.setDescription("Your current balance now: " + BonziUtils.comma(current - amount));
		if(e.isSlashCommand)
			e.slashCommand.replyEmbeds(eb.build()).queue();
		else
			e.channel.sendMessage(eb.build()).queue();
	}
	
}