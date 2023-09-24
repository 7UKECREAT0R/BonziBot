package com.lukecreator.BonziBot.CommandAPI;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class TextChannelArg extends CommandArg {

	public TextChannelArg(String name) {
		super(name);
		this.type = ArgType.Channel;
	}
	
	boolean isMention(String s) {
		int len = s.length();
		if(s.startsWith("<#") && s.endsWith(">") && len > 20 && len < 23) {
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
		int len = s.length();
		try {
			Long.parseLong(s);
			if(len >= 17 && len <= 19)
				return true;
		} catch(NumberFormatException nfe) {}
		return false;
	}
	
	@Override
	public boolean isWordParsable(String word, Guild theGuild) {
		if(this.isValidId(word))
			return true;
		if(this.isMention(word))
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
		if(this.isValidId(word))
			id = Long.parseLong(word);
		else if(this.isMention(word))
			id = this.getMentionId(word);
		
		TextChannel byId = theGuild.getTextChannelById(id);
		this.object = byId;
		return;
	}
	
	@Override
	public String getUsageTerm() {
		if(this.optional)
			return "[#" + this.argName + "]";
		else
			return "<#" + this.argName + ">";
	}
	
	@Override
	public String getErrorDescription() {
		return "Mention a text channel, such as #epic-gaming, or send its ID.";
	}
	
	@Override
	public String stringify(Object obj) {
		if(obj == null)
			return null;
		if(obj instanceof TextChannel) {
			return ((TextChannel)obj).getAsMention();
		} else
			return obj.toString();
	}
}
