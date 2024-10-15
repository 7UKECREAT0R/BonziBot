package com.lukecreator.BonziBot.CommandAPI;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.lukecreator.BonziBot.InternalLogger;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;

/**
 * A single argument for a command.
 */
public abstract class CommandArg {
	
	public enum ArgType {
		Int(OptionType.STRING, true),
		Float(OptionType.INTEGER, false),
		String(OptionType.STRING, false),
		StringAutocomplete(OptionType.STRING, false),
		StringRem(OptionType.STRING, false),
		Boolean(OptionType.BOOLEAN, false),
		User(OptionType.USER, false),
		Role(OptionType.ROLE, false),
		TimeSpan(OptionType.STRING, true),
		Color(OptionType.STRING, true),
		Enum(OptionType.STRING, false),
		Choice(OptionType.STRING, false),
		Channel(OptionType.CHANNEL, false),
		Array(OptionType.UNKNOWN, false),
		Emoji(OptionType.STRING, true);
		
		public final OptionType nativeOption;	// Input information for slash commands.
		public final boolean formatValidate;	// Use bonzi-sided input validation for slash command arguments.
		
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
	 * @param theGuild The guild that this was run in.
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
		this.object = word;
	}
	
	/**
	 *  You generally don't need to override this.
	 * This is how the argument shows up in a string.
	 */
	public String getUsageTerm() {
		char a = this.optional ? '[' : '<';
		char b = this.optional ? ']' : '>';
		return a + this.argName + b;
	}
	/**
	 * Same goes for this. Put an example or something.
	 */
	public String getErrorDescription() {
		return "Incorrect Argument Type.";
	}
	
	/**
	 * {@link #toString()}s this object in the way implemented by this
	 * specific CommandArg. If null is passed in, null will be returned.
	 */
	public String stringify(Object obj) {
		if(obj == null)
			return null;
		return obj.toString();
	}
	
	/**
	 * Clone this CommandArg into a new object.
	 */
	public CommandArg createNew() {
		Class<? extends CommandArg> clazz = this.getClass();
		try {
			CommandArg arg;
			
			if(this.type == ArgType.Enum) {
				EnumArg selfEnum = (EnumArg)this;
				Constructor<?> constructor = clazz.getConstructor(String.class, Class.class);
				arg = (CommandArg)constructor.newInstance(this.argName, selfEnum.baseClass);
				return arg;
			} else if(this.type == ArgType.Choice) {
				String[] choices = ((ChoiceArg)this).choices;
				String[] copy = new String[choices.length];
                System.arraycopy(choices, 0, copy, 0, choices.length);
				Constructor<?> constructor = clazz.getConstructor(String.class, String[].class);
				arg = (CommandArg)constructor.newInstance(this.argName, copy);
				return arg;
			} else if(this.type == ArgType.Int) {
				Constructor<?> constructor = clazz.getConstructor(String.class);
				IntArg intArg = (IntArg)constructor.newInstance(this.argName);
				assert this instanceof IntArg;
				intArg.supportsAll = ((IntArg)this).supportsAll;
				arg = intArg;
			} else {
				Constructor<?> constructor = clazz.getConstructor(String.class);
				arg = (CommandArg)constructor.newInstance(this.argName);
			}
			
			arg.optional = this.optional;
			arg.object = null; // default
			return arg;
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException |
                 IllegalArgumentException | InvocationTargetException e) {
			InternalLogger.printError(e);
		}
        return null;
	}
}