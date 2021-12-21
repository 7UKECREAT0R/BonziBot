package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Gui.GuiHelpMenuCategory;

public class AdminHelpCommand extends Command {
	
	public AdminHelpCommand() {
		this.name = "AdminHelp";
		this.description = "no desc";
		this.category = CommandCategory._HIDDEN;
		this.setCooldown(5000);
		this.adminOnly = true;
	}
	
	@Override
	public void run(CommandExecutionInfo e) {
		GuiHelpMenuCategory menu = new GuiHelpMenuCategory
			(CommandCategory._HIDDEN, e.bonzi);
		BonziUtils.sendGui(e, menu);
	}
	
}
