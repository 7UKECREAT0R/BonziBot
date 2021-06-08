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
		this.category = CommandCategory._HIDDEN;
		this.args = null;
		this.adminOnly = true;
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		
		GuiTestMenu test = new GuiTestMenu();
		BonziUtils.sendGui(e, test);
		
	}
}