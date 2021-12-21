package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.BooleanArg;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;

public class SaveFilesCommand extends Command {

	public SaveFilesCommand() {
		this.subCategory = 0;
		this.name = "Save Files";
		this.unicodeIcon = "💾";
		this.description = "Save data. Can optionally save files as backup.";
		this.args = new CommandArgCollection(new BooleanArg("backup").optional());
		this.adminOnly = true;
		this.category = CommandCategory._HIDDEN;
	}

	@Override
	public void run(CommandExecutionInfo e) {
		boolean backup = e.args.argSpecified("backup")?
			e.args.getBoolean("backup") : false;
		
		e.bonzi.saveData();
		
		if(backup) {
			e.bonzi.saveDataBackup();
			e.channel.sendMessageEmbeds(BonziUtils.successEmbed("Saved backup files.")).queue();
		} else e.channel.sendMessageEmbeds(BonziUtils.successEmbed("Saved regular files.")).queue();
	}
}