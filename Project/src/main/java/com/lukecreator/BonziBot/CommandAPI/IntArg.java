package com.lukecreator.BonziBot.CommandAPI;

import java.util.regex.Pattern;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class IntArg extends CommandArg {
	
	public IntArg(String name) {
		super(name);
		this.type = ArgType.Int;
	}

	public static final String REGEX = "-?[0-9]+";
	public static final Pattern PATTERN = Pattern.compile(REGEX);
	
	@Override
	public boolean isWordParsable(String word) {
		word = word.trim();
		return PATTERN.matcher(word).matches();
	}
	
	@Override
	public void parseWord(String word, JDA jda, User user, Guild theGuild) {
		word = word.trim();
		this.object = Integer.parseInt(word);
		return;
	}
	
	@Override
	public String getErrorDescription() {
		return "Here you can specify any whole number. (2, 11, 146, etc...)";
	}
}
