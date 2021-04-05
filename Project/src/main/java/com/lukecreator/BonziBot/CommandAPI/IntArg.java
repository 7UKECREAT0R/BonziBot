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

	public static final String REGEX = "(?i)-?[0-9,]+[km]?";
	public static final Pattern PATTERN = Pattern.compile(REGEX);
	
	@Override
	public boolean isWordParsable(String word, Guild theGuild) {
		word = word.trim();
		return PATTERN.matcher(word).matches();
	}
	
	@Override
	public void parseWord(String word, JDA jda, User user, Guild theGuild) {
		word = word.trim().replace(",", "").toUpperCase();
		boolean thousand = word.endsWith("K");
		boolean million = word.endsWith("M");
		if(thousand | million)
			word = word.substring(0, word.length() - 1);
		
		try {
			long i = Integer.parseInt(word);
			if(thousand)
				i *= 1000l;
			if(million)
				i *= 1000000l;
			
			if(i > 0x7fffffff)
				this.object = 0x7fffffff;
			else if(i < 0x80000000)
				this.object = 0x80000000;
			else
				this.object = (int)i;
		} catch(NumberFormatException nfe) {
			this.object = 0x7fffffff;
		}
		
		return;
	}
	
	@Override
	public String getErrorDescription() {
		return "Here you can type any whole number. (2, 11, 146, 5k, 20k)";
	}
}
