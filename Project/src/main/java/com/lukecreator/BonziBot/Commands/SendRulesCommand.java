package com.lukecreator.BonziBot.Commands;

import java.util.function.Consumer;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.Rules;
import com.lukecreator.BonziBot.Managers.GuildSettingsManager;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class SendRulesCommand extends Command {
	
	static final Consumer<? super Throwable> ignore = f -> {};
	
	public SendRulesCommand() {
		this.subCategory = 0;
		this.name = "Send Rules";
		this.icon = GenericEmoji.fromEmoji("📖➡");
		this.description = "Send the rules as configured in server settings.";
		this.args = null;
		this.worksInDms = false;
		this.userRequiredPermissions = new Permission[] { Permission.MANAGE_SERVER };
		this.category = CommandCategory.UTILITIES;
		this.setCooldown(BonziUtils.getMsForSeconds(30));
	}

	@Override
	public void run(CommandExecutionInfo e) {
		MessageEmbed msg = BonziUtils.generateRules(e.settings, e.guild, e.bonzi).build();
		GuildSettingsManager gsm = e.bonzi.guildSettings;
		Rules rules = e.settings.getRules();
		
		if(e.isSlashCommand)
			e.slashCommand.reply(":white_check_mark: `Sent rules message.`").setEphemeral(true).queue();
		else
			e.message.delete().queue(null, ignore);
		
		e.channel.sendMessageEmbeds(msg).queue(sent -> {
			rules.setRulesMessage(sent);
			gsm.setSettings(e.guild, e.settings);
		});
	}
}