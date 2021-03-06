package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;

public class JokeCommand extends Command {
	
	public JokeCommand() {
		this.subCategory = 0;
		
		this.name = "joke";
		this.description = "I'll make a cheesy dad joke...";
		this.category = CommandCategory.FUN;
		this.unicodeIcon = "🤣";
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		String joke = e.bonzi.jokes.getJoke();
		e.channel.sendMessage(joke).queue();
	}
}