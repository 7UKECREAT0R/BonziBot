package com.lukecreator.BonziBot.CommandAPI;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class UserArg extends CommandArg {
	
	public UserArg(String name) {
		super(name);
		this.type = ArgType.User;
	}

	boolean isMention(String s) {
		if(s.startsWith("<@!") && s.endsWith(">") &&
			(s.length() == 21 || s.length() == 22)) {
			return true;
		}
		else return false;
	}
	long getMentionId(String mention) {
		String s = mention.substring(3);
		s = s.substring(0, s.length() - 1);
		return Long.parseLong(s);
	}
	boolean isValidId(String s) {
		try {
			Long.parseLong(s);
			if(s.length() == 17 || s.length() == 18) {
				return true;
			}
		} catch(NumberFormatException nfe) {}
		return false;
	}
	
	@Override
	public boolean isWordParsable(String word) {
		if(word.equalsIgnoreCase("me"))
			return true;
		if(isMention(word))
			return true;
		if(isValidId(word))
			return true;
		return false;
	}
	@Override
	public void parseWord(String word, JDA jda, User user, Guild theGuild) {
		
		// Self case.
		if(word.equalsIgnoreCase("me")) {
			this.object = user;
			return;
		}
		
		// Mention case.
		// <@!214183045278728202>
		if(isMention(word)) {
			long id = getMentionId(word);
			User u = jda.getUserById(id);
			this.object = u;
			return;
		}
		
		// ID Case
		// 214183045278728202
		if(isValidId(word)) {
			long id = Long.parseLong(word);
			User u = jda.getUserById(id);
			this.object = u;
			return;
		}
	}
	
	@Override
	public String getUsageTerm() {
		if(this.optional)
			return "[@" + argName + "]";
		else
			return "<@" + argName + ">";
	}
	
	@Override
	public String getErrorDescription() {
		return "You can either mention a user use their ID here. (You can also say \"me\"!)";
	}
}