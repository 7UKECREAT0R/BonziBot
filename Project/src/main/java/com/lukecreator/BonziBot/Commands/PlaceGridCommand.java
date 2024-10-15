package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.TimeSpan;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.EnumArg;
import com.lukecreator.BonziBot.CommandAPI.IntArg;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Managers.GridManager;
import com.lukecreator.BonziBot.Managers.GridManager.TileType;

import net.dv8tion.jda.api.entities.MessageEmbed;

public class PlaceGridCommand extends Command {

	public PlaceGridCommand() {
		this.subCategory = 4;
		this.name = "Place Grid";
		this.icon = GenericEmoji.fromEmoji("⬜");
		this.description = "Place a pixel on the grid.";
		this.args = new CommandArgCollection(new IntArg("x"), new IntArg("y"), new EnumArg("pixel", GridManager.TileType.class));
		this.category = CommandCategory.FUN;
	}

	@Override
	public void run(CommandExecutionInfo e) {
		
		int x = (int) e.args.getInt("x");
		int y = (int) e.args.getInt("y");
		GridManager.TileType pixel = (TileType)e.args.get("pixel");
		
		long time = e.bonzi.grid.timeLeft(e.executor);
		if(time > 0) {
			TimeSpan remaining = TimeSpan.fromMillis(time);
			MessageEmbed me = BonziUtils.failureEmbed("Can't place pixel yet!",
					remaining.toLongString() + " remaining.");
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(me).queue();
			else
				e.channel.sendMessageEmbeds(me).queue();
			return;
		}
		
		try {
			e.bonzi.grid.putTile(x, y, pixel, e.executor);
		} catch (Exception exc) {
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(BonziUtils.failureEmbed("Pixel out of bounds.")).queue();
			else
				e.channel.sendMessageEmbeds(BonziUtils.failureEmbed("Pixel out of bounds.")).queue();
			return;
		}
		
		if(e.isSlashCommand)
			e.slashCommand.replyEmbeds(BonziUtils.successEmbed(pixel.emoji + "  Placed pixel!")).queue();
		else
			e.channel.sendMessageEmbeds(BonziUtils.successEmbed(pixel.emoji + "  Placed pixel!")).queue();
	}
}