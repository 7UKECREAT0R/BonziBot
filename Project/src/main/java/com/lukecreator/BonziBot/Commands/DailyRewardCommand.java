package com.lukecreator.BonziBot.Commands;

import java.awt.Color;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.TimeSpan;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Managers.RewardManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class DailyRewardCommand extends Command {

	public DailyRewardCommand() {
		this.subCategory = 1;
		this.name = "Daily";
		this.unicodeIcon = "☀️";
		this.description = "Claim your daily coins reward! You get extra coins every day that you keep a streak.";
		this.category = CommandCategory.COINS;
	}

	@Override
	public void executeCommand(CommandExecutionInfo e) {
		User u = e.executor;
		RewardManager rm = e.bonzi.rewards;
		
		long timeUntil = rm.timeUntilCanClaim(u);
		if(timeUntil > 0) {
			TimeSpan time = TimeSpan.fromMillis(timeUntil);
			String timeString = time.toLongString();
			MessageEmbed me = BonziUtils.failureEmbed
				("You can't claim your reward yet!", "Time left: " + timeString);
			e.channel.sendMessage(me).queue();
			return;
		}
		
		int earnings = rm.claimAs(u, e.bonzi);
		int newStreak = rm.getStreak(u);
		String streak = BonziUtils.numeral(newStreak);
		String earnString = BonziUtils.comma(earnings);
		
		EmbedBuilder eb = BonziUtils.quickEmbed
			("☀️ Claimed Reward! 🕒", "", Color.yellow);
		String sl = "Streak Level " + streak;
		String re = "Received " + earnString + " coins!";
		eb.addField(sl, re, false);
		
		e.channel.sendMessage(eb.build()).queue();
		return;
	}
}