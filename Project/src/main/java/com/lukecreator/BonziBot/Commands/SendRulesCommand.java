package com.lukecreator.BonziBot.Commands;

import java.util.function.Consumer;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.Rules;
import com.lukecreator.BonziBot.Managers.GuildSettingsManager;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class SendRulesCommand extends Command {
	
	Consumer<? super Throwable> ignore = f -> {};
	
	public SendRulesCommand() {
		this.subCategory = 0;
		this.name = "Send Rules";
		this.unicodeIcon = "";
		this.description = "Send the rules as configured in server settings.";
		this.args = null;
		this.worksInDms = false;
		this.userRequiredPermissions = new Permission[] { Permission.MANAGE_SERVER };
		this.category = CommandCategory._HIDDEN;
	}

	@Override
	public void executeCommand(CommandExecutionInfo e) {
		e.message.delete().queue(null, ignore);
		MessageEmbed msg = BonziUtils.generateRules
			(e.settings, e.guild, e.bonzi).build();
		GuildSettingsManager gsm = e.bonzi.guildSettings;
		Rules rules = e.settings.getRules();
		
		if(e.isSlashCommand)
			e.slashCommand.reply(":white_check_mark: `Sent rules message.`").setEphemeral(true).queue();
		
		e.channel.sendMessageEmbeds(msg).queue(sent -> {
			rules.setRulesMessage(sent);
			gsm.setSettings(e.guild, e.settings);
		});
	}
}