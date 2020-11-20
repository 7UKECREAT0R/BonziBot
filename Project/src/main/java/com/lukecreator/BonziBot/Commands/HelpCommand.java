package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Gui.GuiHelpMenu;

public class HelpCommand extends Command {
	
	public HelpCommand() {
		this.name = "Help";
		this.description = "no desc";
		this.usage = "help";
		this.category = CommandCategory._HIDDEN;
		this.usesArgs = false;
		this.setCooldown(5000);
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		GuiHelpMenu menu = new GuiHelpMenu();
		BonziUtils.sendGuiFromExecutionInfo(e, menu);
	}
	
}
