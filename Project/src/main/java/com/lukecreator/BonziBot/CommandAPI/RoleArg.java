package com.lukecreator.BonziBot.CommandAPI;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class RoleArg extends CommandArg {
	
	public RoleArg(String name) {
		super(name);
		this.type = ArgType.Role;
	}

	boolean isMention(String s) {
		int len = s.length();
		if(s.startsWith("<@&") && s.endsWith(">") && len > 21 && len < 24) {
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
		if(this.isMention(word))
			return true;
		if(this.isValidId(word))
			return true;
		return false;
	}
	@Override
	public void parseWord(String word, JDA jda, User user, Guild theGuild) {
		// Mention case.
		// <@&562661671957561365>
		if(this.isMention(word)) {
			long id = this.getMentionId(word);
			Role r = jda.getRoleById(id);
			this.object = r;
			return;
		}
		
		// ID Case
		// 562661671957561365
		if(this.isValidId(word)) {
			long id = Long.parseLong(word);
			Role r = jda.getRoleById(id);
			this.object = r;
			return;
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
		return "You can either mention a role or use the role's ID.";
	}
	
	@Override
	public String stringify(Object obj) {
		if(obj == null)
			return null;
		if(obj instanceof Role) {
			return ((Role)obj).getAsMention();
		} else
			return obj.toString();
	}
}
