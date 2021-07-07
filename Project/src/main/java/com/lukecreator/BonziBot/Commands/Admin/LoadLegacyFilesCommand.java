package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;

public class LoadLegacyFilesCommand extends Command {
	
	public LoadLegacyFilesCommand() {
		this.name = "loadlegacy";
		this.description = "Loads legacy files.";
		this.category = CommandCategory._HIDDEN;
		this.adminOnly = true;
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		e.bonzi.accounts.loadLegacy();
		e.bonzi.tags.loadLegacy();
		e.message.addReaction("👍").queue();
	}
}
