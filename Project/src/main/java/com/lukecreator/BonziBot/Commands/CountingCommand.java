package com.lukecreator.BonziBot.Commands;

import java.util.List;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.Modifier;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class CountingCommand extends Command {
	
	public CountingCommand() {
		this.subCategory = 0;
		this.name = "Counting";
		this.unicodeIcon = "🎰";
		this.description = "View the current counting game number in this server.";
		this.args = null;
		this.worksInDms = false;
		this.category = CommandCategory.FUN;
	}
	
	@Override
	public void run(CommandExecutionInfo e) {
		
		Guild guild = e.guild;
		List<TextChannel> channels = guild.getTextChannels();
		
		TextChannel counting = null;
		
		out:
		for(TextChannel tc: channels) {
			Modifier[] modifiers = BonziUtils.getChannelModifiers(tc);
			for(Modifier m: modifiers) {
				if(m == Modifier.COUNTING_GAME) {
					counting = tc;
					break out;
				}
			}
		}
		
		if(counting == null) {
			e.reply(BonziUtils.failureEmbed
				("No counting channel in this server!",
				"To make a counting channel, put the words \"counting game\" in your channel topic."));
			return;
		}
		
		int next = e.bonzi.counting.getNextNumber(guild);
		String description = counting.getAsMention() + " `NEXT NUMBER IS " + next + "`";
		EmbedBuilder eb = BonziUtils.quickEmbed("Counting Game", description, BonziUtils.COLOR_BONZI_PURPLE);
		e.reply(eb.build());
	}
	
}
