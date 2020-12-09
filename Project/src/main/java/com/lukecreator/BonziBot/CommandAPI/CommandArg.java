package com.lukecreator.BonziBot.CommandAPI;

import net.dv8tion.jda.api.JDA;

/*
 * A single argument for a command.
 */
public abstract class CommandArg {
	
	public enum ArgType {
		Int, Float, String, StringRem, Boolean, User, Role, TimeSpan
	}
	
	public CommandArg(String name) {
		this.argName = name;
	}
	
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
	public void parseWord(String word, JDA jda) {
		object = word;
	}
	
	/*
	 *  You generally don't need to override this.
	 * This is how the argument shows up in a string.
	 */
	public String getUsageTerm() {
		return "<" + argName + ">";
	}
}
