package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.NoUpload.Constants;

public class HowToIdCommand extends Command {

	public HowToIdCommand() {
		this.subCategory = 0;
		this.name = "How to ID";
		this.unicodeIcon = "🆔";
		this.description = "Show how to get the ID of something.";
		this.args = null;
		this.setCooldown(10000);
		this.category = CommandCategory._HIDDEN;
	}

	@Override
	public void executeCommand(CommandExecutionInfo e) {
		if(e.isGuildMessage) {
			e.channel.sendMessage(BonziUtils.successEmbed("Check DMs (if they're open)!")).queue();
		}
		BonziUtils.messageUser(e.executor,
			  "How to get something's ID.\n\n"
			+ "1) *Hit 'User Settings'*\n"
			+ "2) *Go to the appearance tab.*\n"
			+ "3) *Scroll to the bottom and enable 'Developer Mode'*\n"
			+ "**Now you can right click a role or user and press 'copy id'!**\n\n"
			+ Constants.ID_TUTORIAL);
		
	}
}