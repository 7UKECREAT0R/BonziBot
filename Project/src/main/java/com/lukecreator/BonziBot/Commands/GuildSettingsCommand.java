package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Gui.GuiGuildSettings;

import net.dv8tion.jda.api.entities.Guild;

public class GuildSettingsCommand extends Command {
	
	public GuildSettingsCommand() {
		this.subCategory = 0;
		
		this.name = "Server Settings";
		this.description = "Manage your BonziBot server settings.";
		this.category = CommandCategory.UTILITIES;
		this.unicodeIcon = "⚙️";
		this.worksInDms = false;
		this.moderatorOnly = true;
		
		this.setCooldown(5000);
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		Guild guild = e.guild;
		long guildId = guild.getIdLong();
		String guildName = guild.getName();
		GuiGuildSettings gui = new GuiGuildSettings
				(guildId, guildName);
		BonziUtils.sendGuiFromExecutionInfo(e, gui);
	}
	
}