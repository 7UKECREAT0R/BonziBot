package com.lukecreator.BonziBot.Commands;

import java.awt.Color;
import java.util.Random;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.IntArg;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Managers.UserAccountManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class SlotsCommand extends Command {
	
	Random rand = new Random();
	public enum Slot {
		Banana("🍌"),
		Apple("🍎"),
		Cherry("🍒"),
		Lemon("🍋");
		
		public final String icon;
		Slot(String icon) {
			this.icon = icon;
		}
	}
	
	public SlotsCommand() {
		this.subCategory = 1;
		this.name = "Slots";
		this.icon = GenericEmoji.fromEmoji("🎰");
		this.description = "Play slots, what else needs to be explained?";
		this.args = new CommandArgCollection(new IntArg("amount").supportAll());
		this.category = CommandCategory.COINS;
	}

	@Override
	public void run(CommandExecutionInfo e) {
		UserAccountManager uam = e.bonzi.accounts;
		UserAccount acc = uam.getUserAccount(e.executor);
		long balance = acc.getCoins();
		long amount = e.args.getIsAll("amount") ? balance : e.args.getInt("amount");
		
		if(amount == 0) {
			MessageEmbed me = BonziUtils.failureEmbed("no 0 is allowd");
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(me).queue();
			else
				e.channel.sendMessageEmbeds(me).queue();
			return;
		} else if(amount < 0) {
			MessageEmbed me = BonziUtils.failureEmbed("bro what r u DOING");
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(me).queue();
			else
				e.channel.sendMessageEmbeds(me).queue();
			return;
		}
		if(amount > balance) {
			MessageEmbed me = BonziUtils.failureEmbed("You can't afford that!");
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(me).queue();
			else
				e.channel.sendMessageEmbeds(me).queue();
			return;
		}
		
		String amountS = BonziUtils.comma(amount);
		Slot[] possibleSlots = Slot.values();
		Slot[] picks = new Slot[9];
		int psLen = possibleSlots.length;
		
		for(int i = 0; i < 9; i++) {
			picks[i] = possibleSlots[this.rand.nextInt(psLen)];
		}
		
		int wins = this.getWins(picks);
		double multiplied = 1.5 * wins;
		double dTotalWon = amount * multiplied;
		long totalWon = Math.round(dTotalWon);
		long won = totalWon - amount; 
		String wonS = BonziUtils.comma(won);
		
		if(wins > 0)
			acc.addCoins(won);
		else
			acc.subCoins(amount);
		
		String title;
		Color color;
		switch(wins) {
		case 0:
			title = "No lines...";
			color = Color.red;
			break;
		case 1:
			title = "One Line!";
			color = Color.orange;
			break;
		case 2:
			title = "Double Lines!";
			color = Color.yellow;
			break;
		case 3: 
			title = "TRIPLE Lines!";
			color = Color.green;
			break;
		default:
			title = "MEGA WIN!";
			color = Color.magenta;
		}
		
		String details;
		if(wins == 0) {
			details = "You lost " + amountS + " coins.";
		} else {
			details = multiplied + "x Multiplier! +" + wonS + " coins!";
		}
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(title);
		eb.setColor(color);
		eb.setFooter(details);
		for(int i = 0; i < 9; i++) {
			Slot s = picks[i];
			eb.addField(s.icon, "", true);
		}
		
		if(e.isSlashCommand)
			e.slashCommand.replyEmbeds(eb.build()).queue();
		else
			e.channel.sendMessageEmbeds(eb.build()).queue();
	}
	
	public int getWins(Slot[] slots) {
		
		// Slots Thingies
		// 0 1 2
		// 3 4 5
		// 6 7 8
		
		int wins = 0;
		if(slots[0] == slots[1] && slots[1] == slots[2]) wins++;
		if(slots[3] == slots[4] && slots[4] == slots[5]) wins++;
		if(slots[6] == slots[7] && slots[7] == slots[8]) wins++;
		if(slots[0] == slots[3] && slots[3] == slots[6]) wins++;
		if(slots[1] == slots[4] && slots[4] == slots[7]) wins++;
		if(slots[2] == slots[5] && slots[5] == slots[8]) wins++;
		if(slots[2] == slots[4] && slots[4] == slots[6]) wins++;
		if(slots[0] == slots[4] && slots[4] == slots[8]) wins++;
		return wins;
	}
}