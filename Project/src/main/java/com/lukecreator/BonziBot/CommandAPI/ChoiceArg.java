package com.lukecreator.BonziBot.CommandAPI;

import com.lukecreator.BonziBot.BonziUtils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

/**
 * Similar to the EnumArg but has a unique set of values.
 */
public class ChoiceArg extends CommandArg {
	
	String[] choices;
	
	public ChoiceArg(String name, String...choices) {
		super(name);
		this.type = ArgType.Choice;
		this.choices = choices;
	}
	
	@Override
	public boolean isWordParsable(String word, Guild theGuild) {
		word = word.toUpperCase().replace(' ', '_');
		for(String s: this.choices) {
			if(word.equals(s.toUpperCase()))
				return true;
		}
		return false;
	}
	
	@Override
	public void parseWord(String word, JDA jda, User user, Guild theGuild) {
		word = word.toUpperCase().replace(' ', '_');
		for(String s: this.choices) {
			if(word.equals(s.toUpperCase())) {
				this.object = s;
				return;
			}
		}
		return;
	}
	
	@Override
	public String getErrorDescription() {
		return "This can be any of the following values (or shortened):\n" + BonziUtils.stringJoinOr(", ", this.choices);
	}
	
	public String[] getValues() {
		return this.choices;
	}
}
