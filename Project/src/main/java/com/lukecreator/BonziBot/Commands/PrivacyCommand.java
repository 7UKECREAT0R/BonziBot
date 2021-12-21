package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.BooleanArg;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.EnumArg;
import com.lukecreator.BonziBot.Data.UserAccount;

import net.dv8tion.jda.api.entities.MessageEmbed;

public class PrivacyCommand extends Command {

	public enum PrivacySetting {
		Messages,
		DMs
	}
	
	public PrivacyCommand() {
		this.subCategory = 2;
		this.name = "Privacy";
		this.unicodeIcon = "🔏";
		this.description = "Manage personal privacy settings. (see /terms)";
		this.args = new CommandArgCollection(new EnumArg("setting", PrivacySetting.class), new BooleanArg("enable"));
		this.forcedCommand = true;
		this.category = CommandCategory.UTILITIES;
	}

	@Override
	public void run(CommandExecutionInfo e) {
		PrivacySetting setting = (PrivacySetting)e.args.get("setting");
		boolean enable = e.args.getBoolean("enable");
		
		UserAccount account = e.bonzi.accounts.getUserAccount(e.executor);
		MessageEmbed success = BonziUtils.successEmbed("Changed setting successfully.");
		
		if(setting == PrivacySetting.DMs) {
			
			account.optOutDms = !enable;
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(success).queue();
			else
				e.channel.sendMessageEmbeds(success).queue();
			e.bonzi.accounts.setUserAccount(e.executor, account);
			
		} else if(setting == PrivacySetting.Messages) {
			
			account.optOutExpose = !enable;
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(success).queue();
			else
				e.channel.sendMessageEmbeds(success).queue();
			e.bonzi.accounts.setUserAccount(e.executor, account);
			
		} else {
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(BonziUtils.failureEmbed("Unknown Category.", "Please view the `terms` command.")).queue();
			else
				e.channel.sendMessageEmbeds(BonziUtils.failureEmbed("Unknown Category.", "Please view the `terms` command.")).queue();
		}
	}
}