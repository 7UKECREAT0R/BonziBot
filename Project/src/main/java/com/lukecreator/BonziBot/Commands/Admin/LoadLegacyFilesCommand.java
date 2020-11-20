package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;

public class LoadLegacyFilesCommand extends Command {
	
	public LoadLegacyFilesCommand() {
		this.name = "loadlegacy";
		this.description = "Loads legacy files.";
		this.usage = "loadlegacy";
		this.category = CommandCategory._HIDDEN;
		
		this.usesArgs = false;
		this.adminOnly = true;
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		e.bonzi.accounts.loadLegacy();
	}
}
