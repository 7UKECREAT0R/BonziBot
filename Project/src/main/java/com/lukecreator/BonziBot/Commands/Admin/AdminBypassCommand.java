package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.BooleanArg;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;

public class AdminBypassCommand extends Command {

	public AdminBypassCommand() {
		this.subCategory = 0;
		this.name = "Admin Bypass";
		this.unicodeIcon = "🖥️";
		this.description = "Enable/Disable admins bypassing BonziBot permission limits.";
		this.args = new CommandArgCollection(new BooleanArg("enable"));
		this.category = CommandCategory._HIDDEN;
	}

	@Override
	public void executeCommand(CommandExecutionInfo e) {
		boolean enable = e.args.getBoolean("enable");
		e.bonzi.adminBypassing = enable;
		e.channel.sendMessageEmbeds(BonziUtils.quickEmbed
			("Admin Bypass", "New setting: " + enable,
			BonziUtils.COLOR_BONZI_PURPLE).build()).queue();
		return;
	}
}