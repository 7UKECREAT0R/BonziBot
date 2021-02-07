package com.lukecreator.BonziBot.Data;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.InternalLogger;

/*
 * Generates crappy usernames.
 */
public class UsernameGenerator {
	
	public static final String NAMES_LOCATION = "https://raw.githubusercontent.com/7UKECREAT0R/BonziBot/main/names.txt";
	
	static Random random;
	static String[] names = null;
	static int maxNums = 9999;
	
	public static void init() {
		random = new Random(System.currentTimeMillis());
		
		InternalLogger.print("Downloading usernames for generation...");
		
		try {
			List<String> loader = new ArrayList<String>();
			String downloaded = BonziUtils.getStringFrom(NAMES_LOCATION);
			String[] j = downloaded.split("\n");
			for(String name: j) {
				if(BonziUtils.isWhitespace(name))
					continue;
				loader.add(name);
			}
			names = (String[]) loader.toArray(new String[loader.size()]);
		} catch(FileNotFoundException exc) {
			exc.printStackTrace();
		}
		
		if(names == null)
			InternalLogger.printError("Could not load usernames.", InternalLogger.Severity.FATAL);
		else
			InternalLogger.print("Loaded " + names.length + " usernames.");
	}
	public static String generate() {
		int numbers = random.nextInt(maxNums);
		String name = names[random.nextInt(names.length)];
		return name + numbers;
	}
}
