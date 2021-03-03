package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Gui.GuiHelpMenu;

public class AdminHelpCommand extends Command {
	
	public AdminHelpCommand() {
		this.name = "AdminHelp";
		this.description = "no desc";
		this.category = CommandCategory._HIDDEN;
		this.setCooldown(5000);
		this.adminOnly = true;
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		GuiHelpMenu menu = new GuiHelpMenu(true);
		BonziUtils.sendGui(e, menu);
	}
	
}
