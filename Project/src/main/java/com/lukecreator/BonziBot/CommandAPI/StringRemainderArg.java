package com.lukecreator.BonziBot.CommandAPI;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class StringRemainderArg extends CommandArg {
	
	public StringRemainderArg(String name) {
		super(name);
		this.type = ArgType.StringRem;
	}
	
	@Override
	public boolean isWordParsable(String word, Guild theGuild) {
		return true;
	}
	
	// This should work since StringRemainder is a special argument type.
	@Override
	public void parseWord(String word, JDA jda, User user, Guild theGuild) {}
}
