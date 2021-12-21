package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;

public class TermsCommand extends Command {

	public TermsCommand() {
		this.subCategory = 2;
		this.name = "Terms";
		this.unicodeIcon = "🔒";
		this.description = "Make sure you agree on how I use your information.";
		this.args = null;
		this.forcedCommand = true;
		this.setCooldown(30000);
		this.category = CommandCategory.UTILITIES;
	}

	@Override
	public void run(CommandExecutionInfo e) {
		if(e.isSlashCommand)
			e.slashCommand.replyEmbeds(BonziUtils.TERMS).setEphemeral(true).queue();
		else e.channel.sendMessageEmbeds(BonziUtils.TERMS).queue();
	}
}