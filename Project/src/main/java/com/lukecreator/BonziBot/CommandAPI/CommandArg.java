package com.lukecreator.BonziBot.CommandAPI;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;

/**
 * A single argument for a command.
 */
public abstract class CommandArg {
	
	public enum ArgType {
		Int(OptionType.INTEGER, false),
		Float(OptionType.INTEGER, false),
		String(OptionType.STRING, false),
		StringRem(OptionType.STRING, false),
		Boolean(OptionType.BOOLEAN, false),
		User(OptionType.USER, false),
		Role(OptionType.ROLE, false),
		TimeSpan(OptionType.STRING, true),
		Color(OptionType.STRING, true),
		Enum(OptionType.STRING, false),
		Channel(OptionType.CHANNEL, false),
		Array(OptionType.UNKNOWN, false);
		
		public OptionType nativeOption;	// Input information for slash commands.
		public boolean formatValidate;	// Server-sided input validation for slash commands.
		
		private ArgType(OptionType nativeOption, boolean formatValidate) {
			this.nativeOption = nativeOption;
			this.formatValidate = formatValidate;
		}
	}
	
	public CommandArg(String name) {
		this.argName = name;
	}
	public CommandArg optional() {
		this.optional = true;
		return this;
	}
	public boolean isOptional() {
		return this.optional;
	}
	
	/**
	 * If optional, you can expect the argument to sometimes be null.
	 */
	boolean optional;
	
	public String argName; // The name of this argument.
	public ArgType type; // The BonziBot type of this argument.
	public Object object = null; // The object held in this argument after being parsed.
	
	/**
	 * Return if the string should be
	 *   parsed by this CommandArg.
	 *  
	 * Expect theGuild to be null.
	 * @param theGuild TODO
	 */
	public boolean isWordParsable(String word, Guild theGuild) {
		return false;
	}
	
	/**
	 * Actually parse the string and
	 * store it into an Object value.
	 * 
	 * Expect theGuild to be null.
	 */
	public void parseWord(String word, JDA jda, User user, Guild theGuild) {
		object = word;
	}
	
	/**
	 *  You generally don't need to override this.
	 * This is how the argument shows up in a string.
	 */
	public String getUsageTerm() {
		char a = this.optional ? '[' : '<';
		char b = this.optional ? ']' : '>';
		return a + argName + b;
	}
	/**
	 * Same goes for this. Put an example or something.
	 */
	public String getErrorDescription() {
		return "Incorrect Argument Type.";
	}
}
