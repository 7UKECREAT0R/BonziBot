package com.lukecreator.BonziBot.CommandAPI;

import org.apache.commons.lang3.ArrayUtils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

public class BooleanArg extends CommandArg {
	
	public static final String[] YES = new String[] {
			"yes", "yeah", "true", "enabled", "y"
	};
	public static final String[] NO = new String[] {
			"no", "nope", "false", "disabled", "n"
	};
	public static final String[] ALL = ArrayUtils.addAll(YES, NO);
	
	public BooleanArg(String name) {
		super(name);
		this.type = ArgType.Boolean;
	}
	
	@Override
	public boolean isWordParsable(String word) {
		for(String s: ALL) {
			if(word.equalsIgnoreCase(s))
				return true;
		}
		return false;
	}
	
	@Override
	public void parseWord(String word, JDA jda, User user) {
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
}
