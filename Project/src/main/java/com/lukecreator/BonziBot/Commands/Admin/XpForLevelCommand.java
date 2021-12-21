package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.IntArg;

public class XpForLevelCommand extends Command {

	public XpForLevelCommand() {
		this.subCategory = 0;
		this.name = "Xp For Level";
		this.description = "Get the xp needed for a specific level.";
		this.args = new CommandArgCollection(new IntArg("level"));
		this.category = CommandCategory._HIDDEN;
	}

	@Override
	public void run(CommandExecutionInfo e) {
		int level = e.args.getInt("level");
		int xp = BonziUtils.calculateXpForLevel(level);
		String com = BonziUtils.comma(xp);
		e.channel.sendMessage("`" + com + " XP` needed for level `" + level + "`").queue();
	}
}