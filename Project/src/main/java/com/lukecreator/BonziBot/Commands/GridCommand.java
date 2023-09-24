package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.GenericEmoji;

import net.dv8tion.jda.api.EmbedBuilder;

public class GridCommand extends Command {

	public GridCommand() {
		this.subCategory = 4;
		this.name = "Grid";
		this.icon = GenericEmoji.fromEmoji("🔳");
		this.description = "View the GRID, a grid of pixels where anyone can set one once per hour.";
		this.args = null;
		this.category = CommandCategory.FUN;
		this.setCooldown(BonziUtils.getMsForSeconds(5));
	}

	@Override
	public void run(CommandExecutionInfo e) {
		
		EmbedBuilder eb = BonziUtils.quickEmbed("The Current GRID", "To place a pixel, type `/placegrid <x> <y> <color/emoji>`", BonziUtils.COLOR_BONZI_PURPLE);
		eb.addField("0, 0", e.bonzi.grid.getString(), false);
		
		if(e.isSlashCommand)
			e.slashCommand.replyEmbeds(eb.build()).queue();
		else e.channel.sendMessageEmbeds(eb.build()).queue();
	}
}