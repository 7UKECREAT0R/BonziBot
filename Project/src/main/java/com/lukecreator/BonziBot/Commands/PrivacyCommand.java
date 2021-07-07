package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.BooleanArg;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.StringArg;
import com.lukecreator.BonziBot.Data.UserAccount;

import net.dv8tion.jda.api.entities.MessageEmbed;

public class PrivacyCommand extends Command {

	public PrivacyCommand() {
		this.subCategory = 1;
		this.name = "Privacy";
		this.unicodeIcon = "🔏";
		this.description = "Manage personal privacy settings. (see terms command)";
		this.args = new CommandArgCollection(new StringArg("category"), new BooleanArg("enable"));
		this.forcedCommand = true;
		this.category = CommandCategory.UTILITIES;
	}

	@Override
	public void executeCommand(CommandExecutionInfo e) {
		String category = e.args.getString("category");
		boolean enable = e.args.getBoolean("enable");
		
		UserAccount account = e.bonzi.accounts.getUserAccount(e.executor);
		MessageEmbed success = BonziUtils.successEmbed("Changed setting successfully.");
		
		if(category.equalsIgnoreCase("dm")) {
			
			account.optOutDms = !enable;
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(success).queue();
			else
				e.channel.sendMessage(success).queue();
			e.bonzi.accounts.setUserAccount(e.executor, account);
			
		} else if(category.equalsIgnoreCase("expose")) {
			
			account.optOutExpose = !enable;
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(success).queue();
			else
				e.channel.sendMessage(success).queue();
			e.bonzi.accounts.setUserAccount(e.executor, account);
			
		} else {
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(BonziUtils.failureEmbed("Unknown Category.", "Please view the `terms` command.")).queue();
			else
				e.channel.sendMessage(BonziUtils.failureEmbed("Unknown Category.", "Please view the `terms` command.")).queue();
		}
	}
}