package com.lukecreator.BonziBot.Data;

import java.io.Serializable;

/**
 * utilities for operating with timezones since java's TimeZone sucks
 * @author Lukec
 */
public class TimeZone implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final TimeZone GMT = new TimeZone("Greenwich Mean Time", "GMT", "GMT+0:00", 0);
	public static final TimeZone UTC = new TimeZone("Universal Coordinated Time", "UTC", "GMT+0:00", 0);
	public static final TimeZone ECT = new TimeZone("European Central Time", "ECT", "GMT+1:00", 1 * 60 * 60 * 1000);
	public static final TimeZone EET = new TimeZone("Eastern European Time", "EET", "GMT+2:00", 2 * 60 * 60 * 1000);
	public static final TimeZone ART = new TimeZone("Egypt Standard Time", "ART", "GMT+2:00", 2 * 60 * 60 * 1000);
	public static final TimeZone EAT = new TimeZone("Eastern African Time", "EAT", "GMT+3:00", 3 * 60 * 60 * 1000);
	public static final TimeZone MET = new TimeZone("Middle East Time", "MET", "GMT+3:30", 3 * 60 * 60 * 1500);
	public static final TimeZone NET = new TimeZone("Near East Time", "NET", "GMT+4:00", 4 * 60 * 60 * 1000);
	public static final TimeZone PLT = new TimeZone("Pakistan Lahore Time", "PLT", "GMT+5:00", 5 * 60 * 60 * 1000);
	public static final TimeZone IST = new TimeZone("India Standard Time", "IST", "GMT+5:30", 5 * 60 * 60 * 1500);
	public static final TimeZone BST = new TimeZone("Bangladesh Standard Time", "BST", "GMT+6:00", 6 * 60 * 60 * 1000);
	public static final TimeZone VST = new TimeZone("Vietnam Standard Time", "VST", "GMT+7:00", 7 * 60 * 60 * 1000);
	public static final TimeZone CTT = new TimeZone("China Taiwan Time", "CTT", "GMT+8:00", 8 * 60 * 60 * 1000);
	public static final TimeZone JST = new TimeZone("Japan Standard Time", "JST", "GMT+9:00", 9 * 60 * 60 * 1000);
	public static final TimeZone ACT = new TimeZone("Australia Central Time", "ACT", "GMT+9:30", 9 * 60 * 60 * 1500);
	public static final TimeZone AET = new TimeZone("Australia Eastern Time", "AET", "GMT+9:00", 10 * 60 * 60 * 1000);
	public static final TimeZone SST = new TimeZone("Solomon Standard Time", "SST", "GMT+11:00", 11 * 60 * 60 * 1000);
	public static final TimeZone NST = new TimeZone("New Zealand Standard Time", "NST", "GMT+12:00", 12 * 60 * 60 * 1000);
	public static final TimeZone MIT = new TimeZone("Midway Islands Time", "MIT", "GMT-11:00", -11 * 60 * 60 * 1000);
	public static final TimeZone HST = new TimeZone("Hawaii Standard Time", "HST", "GMT-10:00", -10 * 60 * 60 * 1000);
	public static final TimeZone AST = new TimeZone("Alaska Standard Time", "AST", "GMT-9:00", -9 * 60 * 60 * 1000);
	public static final TimeZone PST = new TimeZone("Pacific Standard Time", "PST", "GMT-8:00", -8 * 60 * 60 * 1000);
	public static final TimeZone PNT = new TimeZone("Phoenix Standard Time", "PNT", "GMT-7:00", -7 * 60 * 60 * 1000);
	public static final TimeZone MST = new TimeZone("Mountain Standard Time", "MST", "GMT-7:00", -7 * 60 * 60 * 1000);
	public static final TimeZone CST = new TimeZone("Central Standard Time", "CST", "GMT-6:00", -6 * 60 * 60 * 1000);
	public static final TimeZone EST = new TimeZone("Eastern Standard Time", "EST", "GMT-5:00", -5 * 60 * 60 * 1000);
	public static final TimeZone IET = new TimeZone("India Eastern Standard Time", "IET", "GMT-5:00", -5 * 60 * 60 * 1000);
	public static final TimeZone PRT1 = new TimeZone("Puerto Rico Time", "PRT", "GMT-4:00", -4 * 60 * 60 * 1000);
	public static final TimeZone PRT2 = new TimeZone("Virgin Islands Time", "PRT", "GMT-4:00", -4 * 60 * 60 * 1000);
	public static final TimeZone CNT = new TimeZone("Canada Newfoundland Time", "CNT", "GMT-3:30", -3 * 60 * 60 * 1500);
	public static final TimeZone AGT = new TimeZone("Argentina Standard Time", "AGT", "GMT-3:00", -3 * 60 * 60 * 1000);
	public static final TimeZone BET = new TimeZone("Brazil Eastern Time", "BET", "GMT-3:00", -3 * 60 * 60 * 1000);
	public static final TimeZone CAT = new TimeZone("Central African Time", "CAT", "GMT-1:00", -1 * 60 * 60 * 1000);
	
	/**
	 * All standard registered timezones.
	 */
	public static final TimeZone[] TIMEZONES = {
		GMT, UTC, ECT, EET, ART, EAT, MET, NET, PLT, IST, BST,
		VST, CTT, JST, ACT, AET, SST, NST, MIT, HST, AST, PST,
		PNT, MST, CST, EST, IET, PRT1, PRT2, CNT, AGT, BET, CAT
	};
	/**
	 * Searches for the first timezone which has a matching abbreviation or name.
	 * @param input
	 * @return `null` if no timezone is found.
	 */
	public static TimeZone searchTimezone(String input) {
		input = input.trim().toUpperCase();
		for(TimeZone zone: TIMEZONES) {
			if(input.equalsIgnoreCase(zone.abbv))
				return zone;
			if(zone.name.toUpperCase().contains(input))
				return zone;
		}
		return null;
	}
	
	public final String name;	// Eastern Standard Time
	public final String abbv;	// EST
	public final String gmt; 	// GMT-5:00
	public final long offsetMs;	// 5 * 60 * 60 * 1000
	
	private TimeZone(String name, String abbv, String gmt, long offsetMs) {
		this.name = name;
		this.abbv = abbv;
		this.gmt = gmt;
		this.offsetMs = offsetMs;
	}
}
