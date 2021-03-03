package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Gui.GuiXpLeaderboard;

public class XpLeaderboardCommand extends Command {

	public XpLeaderboardCommand() {
		this.subCategory = 2;
		this.name = "XP Leaderboard";
		this.unicodeIcon = "🎓🏆";
		this.description = "See who's climbed to the top of the rank ladder!";
		this.category = CommandCategory.FUN;
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		GuiXpLeaderboard lb = new GuiXpLeaderboard(e.bonzi, e.bot);
		BonziUtils.sendGui(e, lb);
	}

}
