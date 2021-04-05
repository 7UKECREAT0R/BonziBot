package com.lukecreator.BonziBot.CommandAPI;

import java.util.regex.Pattern;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class FloatArg extends CommandArg {
	
	public FloatArg(String name) {
		super(name);
		this.type = ArgType.Float;
	}

	public static final String REGEX = "-?[0-9]+\\.?[0-9]*";
	public static final Pattern PATTERN = Pattern.compile(REGEX);
	
	@Override
	public boolean isWordParsable(String word, Guild theGuild) {
		word = word.trim();
		return PATTERN.matcher(word).matches();
	}
	
	@Override
	public void parseWord(String word, JDA jda, User user, Guild theGuild) {
		word = word.trim();
		this.object = Float.parseFloat(word);
		return;
	}
	
	@Override
	public String getErrorDescription() {
		return "Here you should specify any number.";
	}
}
