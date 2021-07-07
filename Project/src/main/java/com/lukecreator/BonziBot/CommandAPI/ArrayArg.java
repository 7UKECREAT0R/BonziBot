package com.lukecreator.BonziBot.CommandAPI;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

/**
 * A little different than other arguments. This
 * one takes an underlying type and creates an
 * array of said type. The values are separated
 * by the ';' character.
 * @author Lukec
 *
 */
public class ArrayArg extends CommandArg {
	
	public static final String DELIMITER = ";";
	public final Class<? extends CommandArg> uType;
	private CommandArg uInstance = null;
	
	public ArrayArg(String name, Class<? extends CommandArg> underlyingType) {
		super(name);
		this.type = ArgType.Array;
		this.uType = underlyingType;
		
		try {
			this.uInstance = underlyingType
				.getConstructor(String.class)
				.newInstance(name);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return;
		}
	}
	
	@Override
	public boolean isWordParsable(String word, Guild theGuild) {
		
		if(this.uInstance == null)
			return true; // prevent deadlocks on users
		
		String[] parts = word.split(DELIMITER);
		
		if(parts.length < 1)
			return false;
		
		for(String part: parts) {
			if(!this.uInstance.isWordParsable(part.trim(), theGuild))
				return false;
		}
		
		return true;
	}
	
	@Override
	public void parseWord(String word, JDA jda, User user, Guild theGuild) {
		if(this.uInstance == null) {
			this.object = new Object[0];
			return;
		}
		
		String[] parts = word.split(DELIMITER);
		uInstance.parseWord(parts[0], jda, user, theGuild);
		Class<?> aType = uInstance.object.getClass();
		Object[] abstractArray = (Object[])Array.newInstance
			(aType, parts.length);
		for(int i = 0; i < parts.length; i++) {
			uInstance.parseWord(parts[i].trim(), jda, user, theGuild);
			abstractArray[i] = uInstance.object;
		}
		this.object = abstractArray;
		return;
	}
	
	@Override
	public String getErrorDescription() {
		return this.uInstance.getErrorDescription() + "\n*Specify multiple values by separating them with a '" + DELIMITER + "'.*";
	}
	
	@Override
	public String getUsageTerm() {
		String a = this.optional ? "[list: " : "<list: ";
		String b = this.optional ? "]" : ">";
		return a + argName + b;
	}
}
