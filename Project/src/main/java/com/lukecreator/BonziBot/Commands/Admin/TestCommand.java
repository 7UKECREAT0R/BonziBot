package com.lukecreator.BonziBot.Commands.Admin;

import java.util.Map.Entry;
import java.util.Set;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Managers.GuildSettingsManager;

public class TestCommand extends Command {
	public TestCommand() {
		this.name = "test";
		this.description = "Tests a feature.";
		this.category = CommandCategory._HIDDEN;
		this.args = null;
		this.adminOnly = true;
	}

	@Override
	public void run(CommandExecutionInfo e) {
		e.reply("Balls");
	}
}