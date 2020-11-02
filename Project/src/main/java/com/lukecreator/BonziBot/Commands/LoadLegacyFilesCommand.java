package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.CommandAPI.ACommand;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;

public class LoadLegacyFilesCommand extends ACommand {
	
	public LoadLegacyFilesCommand() {
		this.name = "loadlegacy";
		this.description = "Loads legacy files.";
		this.usage = "loadlegacy";
		this.category = CommandCategory._TOPLEVEL;
		
		this.usesArgs = false;
		this.adminOnly = true;
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		e.bonzi.accounts.loadLegacy();
	}
}
