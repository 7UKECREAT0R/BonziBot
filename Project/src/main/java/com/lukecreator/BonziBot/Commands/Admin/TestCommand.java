package com.lukecreator.BonziBot.Commands.Admin;

import java.util.Map.Entry;
import java.util.Set;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Managers.GuildSettingsManager;

public class TestCommand extends Command {

	public TestCommand() {
		this.name = "test";
		this.description = "Tests a feature.";
		this.category = CommandCategory._HIDDEN;
		this.args = null;
		this.adminOnly = false;
	}
	
	public void setAllSettings(CommandExecutionInfo e) {
		
		GuildSettingsManager gsm = e.bonzi.guildSettings;
		Set<Entry<Long, GuildSettings>> set = gsm.settings.entrySet();
		
		for(Entry<Long, GuildSettings> entry: set) {
			GuildSettings settings = entry.getValue();
			settings.levellingEnabled = true;
		}
		
		e.reply("All levelling has been enabled.");
	}
	
	@Override
	public void run(CommandExecutionInfo e) {
		this.setAllSettings(e);
	}
}