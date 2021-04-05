package com.lukecreator.BonziBot.CommandAPI;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class TextChannelArg extends CommandArg {

	public TextChannelArg(String name) {
		super(name);
		this.type = ArgType.Channel;
	}
	
	boolean isMention(String s) {
		if(s.startsWith("<#") && s.endsWith(">") &&
			(s.length() == 20 || s.length() == 21)) {
			return true;
		}
		else return false;
	}
	
	long getMentionId(String mention) {
		String s = mention.substring(2);
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
	public boolean isWordParsable(String word, Guild theGuild) {
		if(isValidId(word))
			return true;
		if(isMention(word))
			return true;
		return false;
	}
	
	@Override
	public void parseWord(String word, JDA jda, User user, Guild theGuild) {
		
		if(theGuild == null) {
			this.object = null;
			return;
		}
		
		long id = 0;
		if(isValidId(word))
			id = Long.parseLong(word);
		else if(isMention(word))
			id = getMentionId(word);
		
		TextChannel byId = theGuild.getTextChannelById(id);
		this.object = byId;
		return;
	}
	
	@Override
	public String getUsageTerm() {
		if(this.optional)
			return "[#" + argName + "]";
		else
			return "<#" + argName + ">";
	}
	
	@Override
	public String getErrorDescription() {
		return "Mention a text channel, such as #epic-gaming, or send its ID.";
	}
}
