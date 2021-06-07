package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Gui.GuiGuildSettingsPage1;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;

public class GuildSettingsCommand extends Command {
	
	public GuildSettingsCommand() {
		this.subCategory = 0;
		
		this.name = "Server Settings";
		this.description = "Manage your BonziBot server settings.";
		this.category = CommandCategory.UTILITIES;
		this.unicodeIcon = "⚙️";
		this.worksInDms = false;
		this.userRequiredPermissions = new Permission[] { Permission.MANAGE_SERVER };
		
		this.setCooldown(20000);
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		Guild guild = e.guild;
		long guildId = guild.getIdLong();
		String guildName = guild.getName();
		GuiGuildSettingsPage1 gui = new GuiGuildSettingsPage1
				(guildId, guildName);
		BonziUtils.sendGui(e, gui);
	}
}