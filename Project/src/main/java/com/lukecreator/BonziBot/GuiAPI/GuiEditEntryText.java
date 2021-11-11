package com.lukecreator.BonziBot.GuiAPI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.lukecreator.BonziBot.CommandAPI.CommandArg;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

/**
 * A GuiEditEntry that accepts and parses text as its input.
 * @author Lukec
 *
 */
public class GuiEditEntryText extends GuiEditEntry {
	
	private final CommandArg parser;
	
	@Override
	public String getActionID() {
		return this.parser.argName;
	}
	@Override
	public boolean valueGiven() {
		return this.parser.object != null;
	}
	@Override
	public Object getValue() {
		return this.parser.object;
	}
	@Override
	public String getStringValue() {
		return this.parser.stringify(this.parser.object);
	}
	
	public CommandArg getParser() {
		return this.parser;
	}
	
	public boolean isWordParsable(String word, Guild theGuild) {
		return this.parser.isWordParsable(word, theGuild);
	}
	public void parseWord(String word, JDA jda, User user, Guild theGuild) {
		this.parser.parseWord(word, jda, user, theGuild);
	}
	
	public GuiEditEntryText(CommandArg argument, @Nullable String emoji, @Nonnull String name, @Nonnull String description) {
		this.emoji = emoji;
		this.parser = argument;
		this.title = name;
		this.description = description;
	}
}
