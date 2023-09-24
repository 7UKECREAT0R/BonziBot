package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.BooleanArg;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.GenericEmoji;

public class LoadFilesCommand extends Command {

	public LoadFilesCommand() {
		this.subCategory = 0;
		this.name = "Load Files";
		this.icon = GenericEmoji.fromEmoji("⬆️");
		this.description = "Load data. Can optionally load backup files.";
		this.args = new CommandArgCollection(new BooleanArg("backup").optional());
		this.adminOnly = true;
		this.category = CommandCategory._HIDDEN;
	}

	@Override
	public void run(CommandExecutionInfo e) {
		boolean backup = e.args.argSpecified("backup")?
			e.args.getBoolean("backup") : false;
		
		if(backup) {
			e.bonzi.loadDataBackup();
			e.channel.sendMessageEmbeds(BonziUtils.successEmbed("Loaded backup files.")).queue();
		} else {
			e.bonzi.loadData();
			e.channel.sendMessageEmbeds(BonziUtils.successEmbed("Loaded regular files.")).queue();
		}
	}
}