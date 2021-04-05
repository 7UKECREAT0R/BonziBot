package com.lukecreator.BonziBot.CommandAPI;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class StringArg extends CommandArg {
	
	public StringArg(String name) {
		super(name);
		this.type = ArgType.String;
	}

	@Override
	public boolean isWordParsable(String word, Guild theGuild) {
		return true;
	}
	
	@Override
	public void parseWord(String word, JDA jda, User user, Guild theGuild) {
		this.object = word;
	}
}
