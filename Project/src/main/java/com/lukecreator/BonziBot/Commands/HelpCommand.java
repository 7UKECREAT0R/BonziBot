package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.EnumArg;
import com.lukecreator.BonziBot.Gui.GuiHelpMenu;
import com.lukecreator.BonziBot.Gui.GuiHelpMenuCategory;

public class HelpCommand extends Command {
	
	public HelpCommand() {
		this.name = "Help";
		this.description = "See all the various commands.";
		this.category = CommandCategory._HIDDEN;
		this.args = new CommandArgCollection(new EnumArg("category", CommandCategory.class).optional());
		this.setCooldown(10000);
	}
	
	@Override
	public boolean isRegisterable() {
		return true;
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		if(e.args.argSpecified("category")) {
			CommandCategory c = (CommandCategory)e.args.get("category");
			GuiHelpMenuCategory menu = new GuiHelpMenuCategory(c, e.bonzi);
			BonziUtils.sendGui(e, menu);
		} else {
			GuiHelpMenu menu = new GuiHelpMenu(false);
			BonziUtils.sendGui(e, menu);
		}
	}
	
}