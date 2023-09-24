package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.GenericEmoji;

public class UpdateSlashCommandsCommand extends Command {

	public UpdateSlashCommandsCommand() {
		this.subCategory = 0;
		this.name = "Update Slash Commands";
		this.icon = GenericEmoji.fromEmoji("#️⃣");
		this.description = "Update the slash commands in Discord. Done automatically on test bot.";
		this.args = null;
		this.adminOnly = true;
		this.category = CommandCategory._HIDDEN;
	}

	@Override
	public void run(CommandExecutionInfo e) {
		e.bonzi.slashCommands(e.bot);
		e.channel.sendMessage(":thumbsup:").queue();
	}
}