package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;

public class TestCommand extends Command {

	public TestCommand() {
		this.name = "test";
		this.description = "Tests a feature.";
		this.category = CommandCategory._HIDDEN;
		this.adminOnly = true;
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		EventWaiterManager waiter = e.bonzi.eventWaiter;
		waiter.getConfirmation(e.executor, e.channel, "Are you sure you want to release Big Chungus?", b -> {
			if(b)
				e.channel.sendMessage("**HE HAS BEEN RELEASED.**").queue();
			else
				e.channel.sendMessage("*The world is safe for now.*").queue();
		});
	}
}