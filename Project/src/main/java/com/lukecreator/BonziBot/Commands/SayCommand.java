package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.ArgsComparison;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;

public class SayCommand extends Command {
	
	public SayCommand() {
		this.name = "Say";
		this.description = "Make me say whatever you want!";
		this.usage = "say <text>";
		this.category = CommandCategory.FUN;
		
		this.usesArgs = true;
		this.goalArgs = 1;
		this.argsCheck = ArgsComparison.ANY_HIGHER;
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		String toSay = e.getRemainder();
		e.channel.sendMessage(toSay).queue();
	}
}