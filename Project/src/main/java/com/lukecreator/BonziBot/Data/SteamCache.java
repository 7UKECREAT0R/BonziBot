package com.lukecreator.BonziBot.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.text.similarity.LevenshteinDistance;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.InternalLogger;

/**
 * Holds a record of all the steam games in existance lmao
 * @author Lukec
 */
public class SteamCache {
	
	public class Release {
		public int appID;
		public String appSearch;
		public String appName;
	}
	
	public static final char[] STANDARD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
	
	public static String filterStringForSteamCache(String input) {
		input = input.toUpperCase();
		char[] characters = input.toCharArray();
		
		int resultIndex = 0;
		char[] result = new char[input.length()];
		
		for(int i = 0; i < characters.length; i++) {
			char current = characters[i];
			for(char check: STANDARD_CHARS) {
				if(current == check) {
					result[resultIndex++] = current;
					break;
				}
			}
		}
		
		return new String(result, 0, resultIndex);
	}
	
	private static final LevenshteinDistance DISTANCE = new LevenshteinDistance();
	private static final String DATA_START = "{\"applist\":{\"apps\":[";
	public static final String STORE_URL = "https://store.steampowered.com/app/";
	boolean titlesFetched = false;
	List<Release> titles;
	
	public SteamCache() {
		this.titles = new ArrayList<Release>();
	}
	public void fetchTitles() {
		
		if(this.titlesFetched)
			return;
		
		InternalLogger.print("Fetching steam titles...");
		
		try {
			String bigAssString = this.downloadTitles();
			if(!bigAssString.startsWith(DATA_START)) {
				InternalLogger.print("This is not what the steam cache wanted.");
				return;
			}
			bigAssString = bigAssString.substring(DATA_START.length());
			String[] bigAssArray = bigAssString.split(",");
			
			InternalLogger.print("Beginning processing of steam titles...");
			
			int i = 0;
			Release current = new Release();
			for(String part: bigAssArray) {
				if(i++ % 2 == 0) {
					try {
						current.appID = Integer.parseInt(part.substring(9));
					} catch(NumberFormatException nfe) {
						i++;
						continue;
					} catch(StringIndexOutOfBoundsException sioobe) {
						i++;
						continue;
					}
				} else {
					try {
						current.appName = part.substring(8, part.length() - 2);
						current.appSearch = filterStringForSteamCache(current.appName);
						
						this.titles.add(current);
						current = new Release();
					} catch(StringIndexOutOfBoundsException sioobe) {
						i++;
						continue;
					}
				}
			}
			InternalLogger.print("Cached " + this.titles.size() + " steam release titles.");
			this.titlesFetched = true;
		} catch (IOException e) {
			InternalLogger.printError(e);
		}
	}
	String downloadTitles() throws IOException {
		return BonziUtils.requestGet("http://api.steampowered.com/ISteamApps/GetAppList/v0002/");
	}
	
	public Release searchForTitle(String search) {
		Release bestTitle = null;
		int bestDistance = 10000;
		
		search = filterStringForSteamCache(search);
		
		for(Release release: this.titles) {
			// calculates the levenshtein distance
			short distance = DISTANCE.apply(search, release.appSearch).shortValue();
			
			// cache this release if its better
			if(distance < bestDistance) {
				bestDistance = distance;
				bestTitle = release;
				InternalLogger.print("Narrowed search to " + distance + ", title: " + release.appSearch);
			}
		}
		
		return bestTitle;
	}
}