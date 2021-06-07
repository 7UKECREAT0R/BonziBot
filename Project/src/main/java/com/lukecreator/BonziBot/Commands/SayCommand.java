package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;

public class SayCommand extends Command {
	
	public SayCommand() {
		this.subCategory = 0;
		
		this.name = "Say";
		this.description = "Make me say whatever you want!";
		this.args = CommandArgCollection.single("text");
		this.category = CommandCategory.FUN;
		this.unicodeIcon = "🤖";
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		String toSay = e.args.getString("text");
		if(e.isSlashCommand)
			e.slashCommand.reply(BonziUtils.sanitizeGuildInput(toSay)).queue();
		else
			e.channel.sendMessage(BonziUtils.sanitizeGuildInput(toSay)).queue();
	}
}