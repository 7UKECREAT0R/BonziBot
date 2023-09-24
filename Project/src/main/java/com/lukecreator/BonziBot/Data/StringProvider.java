package com.lukecreator.BonziBot.Data;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.InternalLogger;

/**
 * Need strings? Come here!
 */
public class StringProvider {
	public static final String JOKES_LOCATION = "https://raw.githubusercontent.com/7UKECREAT0R/BonziBot/main/jokes.txt";
	public static final String DICT_LOCATION = "https://raw.githubusercontent.com/7UKECREAT0R/BonziBot/main/dictionary.txt";
	
	String[] jokes;
	String[] dictionary;
	Random random;
	
	public StringProvider() {
		List<String> loader = new ArrayList<String>();
		random = new Random();
		
		InternalLogger.print("Downloading jokes...");
		
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
			InternalLogger.printError(e);
		}
		
		loader.clear();
		InternalLogger.print("Downloading dictionary...");
		
		try {
			String downloaded = BonziUtils.getStringFrom(DICT_LOCATION);
			String[] d = downloaded.split("\n");
			for(String word: d) {
				if(BonziUtils.isWhitespace(word))
					continue;
				loader.add(word);
			}
			dictionary = (String[]) loader.toArray(new String[loader.size()]);
			InternalLogger.print("Loaded " + dictionary.length + " words into dictionary.");
		} catch (FileNotFoundException e) {
			InternalLogger.printError("DICTIONARY FILE NOT LOADED, IS IT MISSING?", InternalLogger.Severity.FATAL);
			InternalLogger.printError(e);
		}
	}
	public String getJoke() {
		return jokes[random.nextInt(jokes.length)];
	}
	public String getWord() {
		return dictionary[random.nextInt(dictionary.length)];
	}
}

