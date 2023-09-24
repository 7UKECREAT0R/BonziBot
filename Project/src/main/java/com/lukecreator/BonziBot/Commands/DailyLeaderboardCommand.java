package com.lukecreator.BonziBot.Commands;

import java.awt.Color;
import java.util.List;
import java.util.Map.Entry;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.GenericEmoji;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

public class DailyLeaderboardCommand extends Command  {
	
	public DailyLeaderboardCommand() {
		this.subCategory = 1;
		this.name = "Daily Leaderboard";
		this.icon = GenericEmoji.fromEmoji("☀🏆");
		this.description = "See the top 10 active daily rewards.";
		this.args = null;
		this.category = CommandCategory.COINS;
	}
	
	@Override
	public void run(CommandExecutionInfo e) {
		
		BonziBot bot = e.bonzi;
		List<Entry<Long, Integer>> accounts = bot.rewards.getTop();
		final int AMOUNT = 10;
		
		EmbedBuilder eb = BonziUtils.quickEmbed("Top Ten Daily!", "The 10 craziest people who use this bot", Color.orange);
		
		for(int i = 0; i < AMOUNT; i++) {
			Entry<Long, Integer> entry = accounts.get(i);
			long id = entry.getKey();
			int amount = entry.getValue();
			User user = e.bot.getUserById(id);
			
			String name;
			if(user == null)
				name = "UNKNOWN USER";
			else
				name = user.getName();
			
			eb.addField((i + 1) + ". " + name, "Current daily: " + amount, false);
		}
		
		e.reply(eb.build());
	}
	
}
