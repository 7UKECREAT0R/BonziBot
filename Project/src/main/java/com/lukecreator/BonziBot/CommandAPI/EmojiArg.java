package com.lukecreator.BonziBot.CommandAPI;

import com.lukecreator.BonziBot.Data.GenericEmoji;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class EmojiArg extends CommandArg {
	
	public EmojiArg(String name) {
		super(name);
		this.type = ArgType.Emoji;
	}
	
	@Override
	public boolean isWordParsable(String word, Guild theGuild) {
		try {
			GenericEmoji.parseEmoji(word);
			return true;
		} catch(Exception exc) {
			return false;
		}
	}
	
	@Override
	public void parseWord(String word, JDA jda, User user, Guild theGuild) {
		try {
			this.object = GenericEmoji.parseEmoji(word);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	
	@Override
	public String getErrorDescription() {
		return "Must be a valid emoji.";
	}
}
