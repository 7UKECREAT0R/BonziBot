package com.lukecreator.BonziBot.Commands;

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
		if(e.isGuildMessage) {
			e.bonzi.guis.sendAndCreateGui(e.tChannel, menu, e.bonzi);
		} else {
			e.bonzi.guis.sendAndCreateGui(e.pChannel, menu, e.bonzi);
		}
	}
	
}
