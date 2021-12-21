package com.lukecreator.BonziBot.Commands;

import java.awt.Color;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.Modifier;

import net.dv8tion.jda.api.EmbedBuilder;

public class ModifiersCommand extends Command {
	
	public ModifiersCommand() {
		this.subCategory = 0;
		this.name = "Modifiers";
		this.unicodeIcon = "🧩";
		this.description = "List of words you can insert into your channel topics to make me act differently!";
		this.category = CommandCategory.UTILITIES;
	}
	
	@Override
	public void run(CommandExecutionInfo e) {
		
		EmbedBuilder eb = BonziUtils.quickEmbed("Available Modifiers", "Put one of these words anywhere in a channel topic to enable them for that channel!"
				+ " Feel free to mix and match with other words to make it look natural. \"You get no XP in this channel.\" will enable NOXP and looks a lot better than \"noxp bruh\"", Color.yellow);
		
		Modifier[] modifiers = Modifier.values();
		for(Modifier mod: modifiers) {
			String name = mod.getDisplayName();
			String icon = mod.icon;
			String desc = mod.desc;
			eb.addField(icon + " " + name, desc, false);
		}
		
		if(e.isSlashCommand)
			e.slashCommand.replyEmbeds(eb.build()).queue();
		else
			e.channel.sendMessageEmbeds(eb.build()).queue();
	}
}
