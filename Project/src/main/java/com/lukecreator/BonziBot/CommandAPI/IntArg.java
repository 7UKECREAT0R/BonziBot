package com.lukecreator.BonziBot.CommandAPI;

import java.util.regex.Pattern;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class IntArg extends CommandArg {
	
	public static final All ALL = new All();
	public static class All {}
	
	protected boolean supportsAll = false;
	
	public IntArg(String name) {
		super(name);
		this.type = ArgType.Int;
	}

	/**
	 * Mark this integer argument as supporting the 'all' keyword. You will need to implement this on a case-by-case basis.
	 * @return The current object for method chaining.
	 */
	public IntArg supportAll() {
		this.supportsAll = true;
		return this;
	}
    /**
	 * Retrieves the value indicating whether the integer argument supports the 'all' keyword.
	 *
	 * @return true if the keyword 'all' is supported, false otherwise.
	 */
	public boolean getSupportsAll() {
        return this.supportsAll;
    }

	public static final String REGEX = "(?i)-?[0-9,]+[km]?";
	public static final Pattern PATTERN = Pattern.compile(REGEX);
	
	@Override
	public boolean isWordParsable(String word, Guild theGuild) {
		word = word.trim();
		
		if(word.equalsIgnoreCase("all") && this.supportsAll)
			return true;
		
		return PATTERN.matcher(word).matches();
	}
	
	@Override
	public void parseWord(String word, JDA jda, User user, Guild theGuild) {
		word = word.trim();
		
		if(word.equalsIgnoreCase("all") && this.supportsAll) {
			// used the 'all' keyword
			this.object = ALL;
		} else {
			word = word.trim().replace(",", "").toUpperCase();
			boolean thousand = word.endsWith("K");
			boolean million = word.endsWith("M");
			if(thousand | million)
				word = word.substring(0, word.length() - 1);

			try {
				long i = Integer.parseInt(word);
				if(thousand)
					i *= 1000L;
				if(million)
					i *= 1000000L;
				
				this.object = i;
			} catch(NumberFormatException nfe) {
				this.object = 0;
			}
		}
    }
	
	@Override
	public String getErrorDescription() {
		if(this.supportsAll)
			return "Use any whole number (2, 11, 146, 5k, 20k), or 'all'.";
		else
			return "Use any whole number. (2, 11, 146, 5k, 20k)";
	}
	
	@Override
	public String stringify(Object obj) {
		if(obj == null)
			return null;
		if(obj instanceof Integer) {
			return String.valueOf(((Integer)obj).intValue());
		} else if(obj instanceof Long) {
			return String.valueOf(((Long)obj).longValue());
		} else if(obj instanceof All) {
			return "all";
		} else
			return obj.toString();
	}
}
