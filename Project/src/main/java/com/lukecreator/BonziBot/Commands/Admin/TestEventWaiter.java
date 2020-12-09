package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;

public class TestEventWaiter extends Command {
	
	public TestEventWaiter() {
		this.name = "testeventwaiter";
		this.description = "Tests the EventWaiter system.";
		this.category = CommandCategory._HIDDEN;
		this.adminOnly = true;
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		e.channel.sendMessage("awaiting a response...").queue();
		
		EventWaiterManager mg = e.bonzi.eventWaiter;
		mg.waitForResponse(e.executor, msg -> {
			msg.addReaction("👍").queue();
			msg.getChannel().sendMessage("Got your message!").queue();
		});
	}
}
