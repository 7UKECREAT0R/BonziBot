package com.lukecreator.BonziBot.CommandAPI;

import net.dv8tion.jda.api.JDA;

public class StringArg extends CommandArg {
	
	public StringArg(String name) {
		super(name);
		this.type = ArgType.String;
	}

	@Override
	public boolean isWordParsable(String word) {
		return true;
	}
	
	@Override
	public void parseWord(String word, JDA jda) {
		this.object = word;
	}
}
