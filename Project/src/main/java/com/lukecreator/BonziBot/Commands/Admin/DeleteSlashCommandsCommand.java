package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;

public class DeleteSlashCommandsCommand extends Command {

	public DeleteSlashCommandsCommand() {
		this.subCategory = 0;
		this.name = "Delete Slash Commands";
		this.unicodeIcon = "#️⃣";
		this.description = "Delete the slash commands in Discord. Should do this on the test bot before pushing to the main one.";
		this.args = null;
		this.adminOnly = true;
		this.category = CommandCategory._HIDDEN;
	}

	@Override
	public void run(CommandExecutionInfo e) {
		e.bot.updateCommands().queue();
		e.channel.sendMessage(":x: deleted").queue();
	}
}