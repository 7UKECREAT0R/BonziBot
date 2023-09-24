package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.GenericEmoji;

public class ResetGridCooldownsCommand extends Command {

	public ResetGridCooldownsCommand() {
		this.subCategory = 0;
		this.name = "Reset Grid Cooldowns";
		this.icon = GenericEmoji.fromEmoji("🚫");
		this.description = "resets all the grid cooldowns. used for testing";
		this.args = null;
		this.category = CommandCategory._HIDDEN;
		this.adminOnly = true;
	}

	@Override
	public void run(CommandExecutionInfo e) {
		e.bonzi.grid.resetCooldowns();
		e.channel.sendMessage("reset em").queue();
	}
}