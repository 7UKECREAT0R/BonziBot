package com.lukecreator.BonziBot.CommandAPI;

import java.util.List;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class UserArg extends CommandArg {
	
	public UserArg(String name) {
		super(name);
		this.type = ArgType.User;
	}

	boolean isMention(String s) {
		int len = s.length();
		if(s.startsWith("<@") && s.endsWith(">") && len > 20 && len < 23)
			return true;
		if(s.startsWith("<@!") && s.endsWith(">") && len > 21 && len < 24)
			return true;
		else return false;
	}
	long getMentionId(String mention) {
		if(mention.startsWith("<@!")) {
			String s = mention.substring(3);
			s = s.substring(0, s.length() - 1);
			return Long.parseLong(s);
		} else {
			String s = mention.substring(2);
			s = s.substring(0, s.length() - 1);
			return Long.parseLong(s);
		}
		
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
		if(word == null)
			return false;
		if(word.equalsIgnoreCase("me"))
			return true;
		if(this.isMention(word))
			return true;
		if(this.isValidId(word))
			return true;
		
		if(theGuild != null) {
			List<Member> members = theGuild.getMembersByEffectiveName(word, true);
			if(!members.isEmpty())
				return true;
			members = theGuild.getMembersByName(word, true);
			if(!members.isEmpty())
				return true;
		}
		
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
		if(this.isMention(word)) {
			long id = this.getMentionId(word);
			User u = jda.getUserById(id);
			this.object = u;
			return;
		}
		
		// ID Case
		// 214183045278728202
		if(this.isValidId(word)) {
			long id = Long.parseLong(word);
			User u = jda.getUserById(id);
			this.object = u;
			return;
		}
		
		// Search guild for user.
		if(theGuild != null) {
			List<Member> members = theGuild.getMembersByEffectiveName(word, true);
			if(!members.isEmpty()) {
				this.object = members.get(0).getUser();
				return;
			}
			members = theGuild.getMembersByName(word, true);
			if(!members.isEmpty()) {
				this.object = members.get(0).getUser();
				return;
			}
		}
	}
	
	@Override
	public String getUsageTerm() {
		if(this.optional)
			return "[@" + this.argName + "]";
		else
			return "<@" + this.argName + ">";
	}
	
	@Override
	public String getErrorDescription() {
		return "You can either mention a user, type their name, or use their ID here. (You can also say \"me\"!)";
	}
	
	@Override
	public String stringify(Object obj) {
		if(obj == null)
			return null;
		if(obj instanceof User) {
			return ((User)obj).getAsMention();
		} else
			return obj.toString();
	}
}