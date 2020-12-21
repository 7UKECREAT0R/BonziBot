package com.lukecreator.BonziBot.CommandAPI;

import com.lukecreator.BonziBot.TimeSpan;

import net.dv8tion.jda.api.JDA;

public class TimeSpanArg extends CommandArg {
	
	public TimeSpanArg(String name) {
		super(name);
		this.type = ArgType.TimeSpan;
	}
	
	@Override
	public boolean isWordParsable(String word) {
		return TimeSpan.stringCanBeParsed(word);
	}
	
	@Override
	public void parseWord(String word, JDA jda) {
		this.object = TimeSpan.parseTimeSpan(word);
	}
	
	@Override
	public String getErrorDescription() {
		return "Specify a time here. (10s, 3m, 5h, etc...)";
	}
}
