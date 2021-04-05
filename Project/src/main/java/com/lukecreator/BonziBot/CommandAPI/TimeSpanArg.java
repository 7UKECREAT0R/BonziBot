package com.lukecreator.BonziBot.CommandAPI;

import com.lukecreator.BonziBot.TimeSpan;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class TimeSpanArg extends CommandArg {
	
	public TimeSpanArg(String name) {
		super(name);
		this.type = ArgType.TimeSpan;
	}
	
	@Override
	public boolean isWordParsable(String word, Guild theGuild) {
		return TimeSpan.stringCanBeParsed(word);
	}
	
	@Override
	public void parseWord(String word, JDA jda, User user, Guild theGuild) {
		this.object = TimeSpan.parseTimeSpan(word);
	}
	
	@Override
	public String getErrorDescription() {
		return "Specify a time here. (10s, 3m, 5h, etc...)";
	}
}
