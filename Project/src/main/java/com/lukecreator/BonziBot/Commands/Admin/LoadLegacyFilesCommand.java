package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;

import net.dv8tion.jda.api.entities.emoji.Emoji;

public class LoadLegacyFilesCommand extends Command {
	
	public LoadLegacyFilesCommand() {
		this.name = "loadlegacy";
		this.description = "Loads legacy files.";
		this.category = CommandCategory._HIDDEN;
		this.adminOnly = true;
	}
	
	@Override
	public void run(CommandExecutionInfo e) {
		e.bonzi.accounts.loadLegacy();
		e.bonzi.tags.loadLegacy();
		e.bonzi.counting.loadLegacy();
		e.message.addReaction(Emoji.fromUnicode("👍")).queue();
	}
}
