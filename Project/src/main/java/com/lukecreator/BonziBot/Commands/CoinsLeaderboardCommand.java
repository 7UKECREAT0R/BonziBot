package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.BooleanArg;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArg;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Gui.GuiCoinsLeaderboard;

import net.dv8tion.jda.api.entities.Guild;

public class CoinsLeaderboardCommand extends Command {
	
	public CoinsLeaderboardCommand() {
		this.subCategory = 1;
		this.name = "Coins Leaderboard";
		this.icon = GenericEmoji.fromEmoji("🟡🏆");
		this.description = "See the richest users of all time!";
		this.args = CommandArgCollection.fromArray(new CommandArg[] { new BooleanArg("local").optional() });
		this.category = CommandCategory.COINS;
		this.setCooldown(5000);
	}
	
	@Override
	public void run(CommandExecutionInfo e) {
		Guild g = e.guild;
		
		long gId;
		boolean local;
		
		if(e.args.argSpecified("local")) {
			local = e.args.getBoolean("local");
			gId = local ? g.getIdLong() : -1L;
		} else {
			local = false;
			gId = -1L;
		}
		
		GuiCoinsLeaderboard lb = new GuiCoinsLeaderboard(e.bonzi, e.bot, gId);
		BonziUtils.sendGui(e, lb);
	}
	
}
