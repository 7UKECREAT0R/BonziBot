package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.CommandAPI.ArrayArg;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.TextChannelArg;

import net.dv8tion.jda.api.entities.TextChannel;

public class TestCommand extends Command {

	public TestCommand() {
		this.name = "test";
		this.description = "Tests a feature.";
		this.category = CommandCategory._HIDDEN;
		this.args = new CommandArgCollection(
			new ArrayArg("channels", TextChannelArg.class)
		);
		this.adminOnly = true;
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		
		TextChannel[] channels = (TextChannel[])e.args.getArray("channels");
		
		for(TextChannel tc: channels) {
			tc.sendMessage("hello").queue();
		}
		
	}
}