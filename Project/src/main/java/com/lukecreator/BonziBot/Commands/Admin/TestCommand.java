package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Gui.GuiTestMenu;

public class TestCommand extends Command {

	public TestCommand() {
		this.name = "test";
		this.description = "Tests a feature.";
		this.usage = "test";
		this.category = CommandCategory._HIDDEN;
		
		this.usesArgs = false;
		this.adminOnly = true;
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		GuiTestMenu menu = new GuiTestMenu();
		BonziUtils.sendGuiFromExecutionInfo(e, menu);
	}
	
}
