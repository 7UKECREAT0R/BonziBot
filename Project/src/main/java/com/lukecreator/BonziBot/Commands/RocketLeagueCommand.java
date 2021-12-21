package com.lukecreator.BonziBot.Commands;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.EnumArg;

import net.dv8tion.jda.api.EmbedBuilder;

public class RocketLeagueCommand extends Command {
	
	public enum RLRegion {
		EUROPE,
		US
	}
	
	public RocketLeagueCommand() {
		this.subCategory = 0;
		this.name = "Rocket League";
		this.unicodeIcon = "<:rocketleague:889250974970044447>";
		this.description = "View next rocket league tournament times.";
		this.args = new CommandArgCollection(new EnumArg("region", RLRegion.class));
		this.category = CommandCategory.UTILITIES;
	}

	@Override
	public void run(CommandExecutionInfo e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.cyan);
		
		LocalDateTime today = LocalDateTime.now(ZoneOffset.UTC).toLocalDate().atTime(LocalTime.MIN);
		long start = today.toEpochSecond(ZoneOffset.UTC);
		
		RLRegion region = (RLRegion)e.args.get("region");
		
		if(region == RLRegion.EUROPE) {
			eb.setTitle("Tournament Times in Europe");
			eb.addField("<t:" + (start + 50400) + ":R>", "Every Day", false);
			eb.addField("<t:" + (start + 57600) + ":R>", "Every Day", false);
			eb.addField("<t:" + (start + 64800) + ":R>", "Every Day", false);
			eb.addField("<t:" + (start + 72000) + ":R>", "Every Day", false);
			eb.addField("<t:" + (start + 79200) + ":R>", "Every Day", false);
		} else {
			eb.setTitle("Tournament Times in US");
			eb.addField("<t:" + (start + 57600) + ":R>", "Saturday/Sunday", false);
			eb.addField("<t:" + (start + 68400) + ":R>", "Saturday/Sunday", false);
			eb.addField("<t:" + (start + 79200) + ":R>", "Every Day", false);
			eb.addField("<t:" + (start + 90000) + ":R>", "Every Day", false);
			eb.addField("<t:" + (start + 100800) + ":R>", "Every Day", false);
		}
		
		if(e.isSlashCommand)
			e.slashCommand.replyEmbeds(eb.build()).queue();
		else e.channel.sendMessageEmbeds(eb.build()).queue();
	}
}