package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.UserArg;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;

import net.dv8tion.jda.api.entities.User;

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
		
		EventWaiterManager ewm = e.bonzi.eventWaiter;
		e.channel.sendMessage("send a user").queue();
		ewm.waitForArgument(e.executor, new UserArg(""), _user -> {
			User user = (User)_user;
			e.channel.sendMessage("tag: " + user.getAsTag() + "\navatar: " + user.getEffectiveAvatarUrl()).queue();
		});
		
	}
}