package com.lukecreator.BonziBot.Commands.Admin;

import java.util.ArrayList;
import java.util.List;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.Modifier;

public class TestCommand extends Command {

	public TestCommand() {
		this.name = "test";
		this.description = "Tests a feature.";
		this.category = CommandCategory._HIDDEN;
		this.adminOnly = true;
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		List<String> ls = new ArrayList<String>();
		for(Modifier mod: e.modifiers) {
			ls.add(mod.getDisplayName());
		}
		String s = String.join(", ", ls);
		e.channel.sendMessage("Modifiers: " + s).queue();
	}
	
}
