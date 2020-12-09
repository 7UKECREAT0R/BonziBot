package com.lukecreator.BonziBot.Data;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.InternalLogger;

/*
 * Need jokes? Come here!
 */
public class JokeProvider {
	public static final String JOKES_LOCATION = "https://raw.githubusercontent.com/7UKECREAT0R/BonziBot/main/jokes.txt";
	String[] jokes;
	Random random;
	public JokeProvider() {
		List<String> loader = new ArrayList<String>();
		random = new Random();
		
		try {
			String downloaded = BonziUtils.getStringFrom(JOKES_LOCATION);
			String[] j = downloaded.split("\n");
			for(String joke: j) {
				if(BonziUtils.isWhitespace(joke))
					continue;
				loader.add(joke);
			}
			jokes = (String[]) loader.toArray(new String[loader.size()]);
			InternalLogger.print("Loaded " + jokes.length + " jokes.");
		} catch (FileNotFoundException e) {
			InternalLogger.printError("JOKES FILE NOT LOADED, IS IT MISSING?", InternalLogger.Severity.FATAL);
			e.printStackTrace();
		}
	}
	public String getJoke() {
		return jokes[random.nextInt(jokes.length)];
	}
}

