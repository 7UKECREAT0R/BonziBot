package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.CommandAPI.BooleanArg;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;

public class TestCommand extends Command {

	public TestCommand() {
		this.name = "test";
		this.description = "Tests a feature.";
		this.args = new CommandArgCollection(
				new BooleanArg("big chungus gamer"),
				new BooleanArg("smol gamer"));
		this.category = CommandCategory._HIDDEN;
		this.adminOnly = true;
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		/*GuiTestMenu menu = new GuiTestMenu();
		BonziUtils.sendGuiFromExecutionInfo(e, menu);*/
		
		boolean bg = e.args.getBoolean("big chungus gamer");
		boolean sg = e.args.getBoolean("smol gamer");
		e.channel.sendMessage("Big chungus gamer: " + bg + "\nSmol gamer: " + sg).queue();
	}
	
}
