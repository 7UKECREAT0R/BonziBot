package com.lukecreator.BonziBot.CommandAPI;

import org.apache.commons.lang3.ArrayUtils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class BooleanArg extends CommandArg {
	
	public static final String[] YES = new String[] {
			"yes", "yeah", "true", "enabled", "y", "enable", "on", "yea", "yep"
	};
	public static final String[] NO = new String[] {
			"no", "nope", "false", "disabled", "n", "disable", "off", "nah", "na"
	};
	public static final String[] ALL = ArrayUtils.addAll(YES, NO);
	
	public static boolean parseBoolean(String str) {
		for(String s: YES) {
			if(str.equalsIgnoreCase(s)) {
				return true;
			}
		}
		for(String s: NO) {
			if(str.equalsIgnoreCase(s)) {
				return false;
			}
		}
		
		return false;
	}
	
	public BooleanArg(String name) {
		super(name);
		this.type = ArgType.Boolean;
	}
	
	@Override
	public boolean isWordParsable(String word, Guild theGuild) {
		for(String s: ALL) {
			if(word.equalsIgnoreCase(s))
				return true;
		}
		return false;
	}
	
	@Override
	public void parseWord(String word, JDA jda, User user, Guild theGuild) {
		for(String s: YES) {
			if(word.equalsIgnoreCase(s)) {
				this.object = true;
				return;
			}
		}
		for(String s: NO) {
			if(word.equalsIgnoreCase(s)) {
				this.object = false;
				return;
			}
		}
		object = false;
	}
	
	@Override
	public String getErrorDescription() {
		return "You can put a \"yes\", \"no\", \"true\", \"false\", etc... here.";
	}
	
	@Override
	public String stringify(Object obj) {
		if(obj == null)
			return null;
		if(obj instanceof Boolean) {
			return ((Boolean)obj).booleanValue() ? "true" : "false";
		} else
			return obj.toString();
	}
}