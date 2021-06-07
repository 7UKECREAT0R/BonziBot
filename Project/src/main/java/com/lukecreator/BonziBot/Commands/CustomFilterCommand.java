package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;

public class CustomFilterCommand extends Command {

	public CustomFilterCommand() {
		this.subCategory = 1;
		this.name = "Custom Filter";
		this.unicodeIcon = "🗒️";
		this.description = "List of words that will automatically be removed. Can also be accessed through serversettings.";
		this.args = null;
		this.category = CommandCategory.UTILITIES;
	}

	@Override
	public void executeCommand(CommandExecutionInfo e) {
		// TODO make it
	}
}