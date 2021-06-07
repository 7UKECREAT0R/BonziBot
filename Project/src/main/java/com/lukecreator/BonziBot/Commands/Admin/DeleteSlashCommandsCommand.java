package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;

public class DeleteSlashCommandsCommand extends Command {

	public DeleteSlashCommandsCommand() {
		this.subCategory = 0;
		this.name = "Delete Slash Commands";
		this.unicodeIcon = "🔥";
		this.description = "Delete all slash commands on clients.";
		this.args = null;
		this.category = CommandCategory._HIDDEN;
		this.adminOnly = true;
	}

	@Override
	public void executeCommand(CommandExecutionInfo e) {
		e.bot.updateCommands().queue();
		e.channel.sendMessage("deleting.. could take upto an hour.").queue();
	}
}