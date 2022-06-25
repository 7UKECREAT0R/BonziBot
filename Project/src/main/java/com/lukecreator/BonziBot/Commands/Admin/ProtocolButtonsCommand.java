package com.lukecreator.BonziBot.Commands.Admin;

import java.awt.Color;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class ProtocolButtonsCommand extends Command {

	public ProtocolButtonsCommand() {
		this.subCategory = 0;
		this.name = "Protocol Buttons";
		this.unicodeIcon = "";
		this.description = "Buttons for Discord Protocol (messing around)";
		this.args = null;
		this.category = CommandCategory._HIDDEN;
	}

	@Override
	public void run(CommandExecutionInfo e) {
		MessageEmbed me = BonziUtils.quickEmbed("hi", "protocol butons!", Color.pink).build();
		
		e.channel.sendMessageEmbeds(me).setActionRow(
			Button.link("discord://-/settings/profile-customization", "Customize Profile"),
			Button.link("discord://-/guilds/create", "New Server"),
			Button.link("discord://-/channels/@me/", "View Friends")
		).queue();
	}
}