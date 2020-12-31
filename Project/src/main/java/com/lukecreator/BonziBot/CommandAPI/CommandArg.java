package com.lukecreator.BonziBot.CommandAPI;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

/*
 * A single argument for a command.
 */
public abstract class CommandArg {
	
	public enum ArgType {
		Int, Float, String, StringRem, Boolean, User, Role, TimeSpan, Color, Enum
	}
	
	public CommandArg(String name) {
		this.argName = name;
	}
	public CommandArg optional() {
		this.optional = true;
		return this;
	}
	
	/*
	 * If optional, you can expect the argument to sometimes be null.
	 */
	boolean optional;
	
	public String argName;
	public ArgType type;
	public Object object = null;
	
	/*
	 * Return if the string should be
	 *   parsed by this CommandArg.
	 */
	public boolean isWordParsable(String word ) {
		return false;
	}
	
	/*
	 * Actually parse the string and
	 * store it into an Object value.
	 */
	public void parseWord(String word, JDA jda, User user) {
		object = word;
	}
	
	/*
	 *  You generally don't need to override this.
	 * This is how the argument shows up in a string.
	 */
	public String getUsageTerm() {
		char a = this.optional ? '[' : '<';
		char b = this.optional ? ']' : '>';
		return a + argName + b;
	}
	/*
	 * Same goes for this. Put an example or something.
	 */
	public String getErrorDescription() {
		return "Incorrect Argument Type.";
	}
}
