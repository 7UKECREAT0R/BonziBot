package com.lukecreator.BonziBot.Commands;

import java.util.function.Consumer;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.GuildSettings;
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
		MessageEmbed msg = BonziUtils.generateRules(e.guild, e.bonzi).build();
		GuildSettingsManager gsm = e.bonzi.guildSettings;
		GuildSettings settings = gsm.getSettings(e.guild);
		Rules rules = settings.getRules();
		e.channel.sendMessage(msg).queue(sent -> {
			rules.setRulesMessage(sent);
			gsm.setSettings(e.guild, settings);
		});
	}
}