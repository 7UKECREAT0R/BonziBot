package com.lukecreator.BonziBot.CommandAPI;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;

public class RoleArg extends CommandArg {
	
	public RoleArg(String name) {
		super(name);
		this.type = ArgType.Role;
	}

	boolean isMention(String s) {
		if(s.startsWith("<@&") && s.endsWith(">") &&
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
			if(s.length() == 17 || s.length() == 18)
				return true;
		} catch(NumberFormatException nfe) {}
		return false;
	}
	
	@Override
	public boolean isWordParsable(String word) {
		if(isMention(word))
			return true;
		if(isValidId(word))
			return true;
		return false;
	}
	@Override
	public void parseWord(String word, JDA jda) {
		// Mention case.
		// <@&562661671957561365>
		if(isMention(word)) {
			long id = getMentionId(word);
			Role r = jda.getRoleById(id);
			this.object = r;
		}
		
		// ID Case
		// 562661671957561365
		if(isValidId(word)) {
			long id = Long.parseLong(word);
			Role r = jda.getRoleById(id);
			this.object = r;
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
		return "You can either mention a role or use the role's ID.";
	}
}
