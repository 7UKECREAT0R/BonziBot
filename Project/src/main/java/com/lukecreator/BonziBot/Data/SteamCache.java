package com.lukecreator.BonziBot.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.InternalLogger;

/**
 * Holds a record of all the steam games in existance lmao
 * @author Lukec
 */
public class SteamCache {
	
	public class Release {
		public int appID;
		public String appName;
	}
	
	private static final String DATA_START = "{\"applist\":{\"apps\":[";
	public static final String STORE_URL = "https://store.steampowered.com/app/";
	boolean titlesFetched = false;
	List<Release> titles;
	
	public SteamCache() {
		this.titles = new ArrayList<Release>();
	}
	public void fetchTitles() {
		
		if(titlesFetched)
			return;
		
		InternalLogger.print("[STEAM] Fetching steam titles...");
		
		try {
			String bigAssString = this.downloadTitles();
			if(!bigAssString.startsWith(DATA_START)) {
				InternalLogger.print("[STEAM] This is not what I wanted.");
				return;
			}
			bigAssString = bigAssString.substring(DATA_START.length());
			String[] bigAssArray = bigAssString.split(",");
			
			InternalLogger.print("[STEAM] Beginning processing...");
			
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
						current.appName = part.substring(8, part.length() - 2).toUpperCase();
						this.titles.add(current);
						current = new Release();
					} catch(StringIndexOutOfBoundsException sioobe) {
						i++;
						continue;
					}
				}
			}
			InternalLogger.print("[STEAM] Cached " + this.titles.size() + " release titles.");
			titlesFetched = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	String downloadTitles() throws IOException {
		return BonziUtils.requestGet("http://api.steampowered.com/ISteamApps/GetAppList/v0002/");
	}

	public Release[] searchForTitles(String search) {
		
		search = search.toUpperCase();
		List<Release> found = new ArrayList<Release>();
		
		for(Release release: this.titles) {
			if(release.appName.contains(search))
				found.add(release);
		}
		
		return (Release[]) found.toArray(new Release[found.size()]);
	}
}