package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Managers.RewardManager;

public class TestCommand extends Command {

	public TestCommand() {
		this.name = "test";
		this.description = "Tests a feature.";
		this.category = CommandCategory._HIDDEN;
		this.adminOnly = true;
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		RewardManager rw = e.bonzi.rewards;
		rw.setLastCollectionTime(e.executor.getIdLong(),
			System.currentTimeMillis() - RewardManager.ONE_DAY);
		e.channel.sendMessage("set last collection time to 24 hours ago").queue();
	}
	
}
