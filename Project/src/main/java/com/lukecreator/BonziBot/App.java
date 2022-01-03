package com.lukecreator.BonziBot;

import java.security.GeneralSecurityException;

public class App {
	
	/**
	 * This is only here for legacy data conversion.
	 *    It has no real use in the actual bot.
	 */
	public enum PremiumCommand {
		Nickall, // <Server Owner Only> Nicknames every user on the discord. Only server owners can use this command!
		RainbowRole, // <Server Owner Only> Usage: b:rainbowrole @role. Makes the targeted role rainbow!
		SuperPlay, // Puts your song at the front of the queue!
		Expose, // Exposes the last deleted message!
		Profilepic, // Gets the user's profile picture.
		Troll, // Troll a user with a random troll.
		Comment, // "Comment" on a video.
		
		Premium // Gives all special commands in a single package + all bonzibot premium perks. Can be bought with irl money, or coins.
	}
	
	public static boolean DEBUG = false;
	
	public static void main(String[] args) throws InterruptedException, GeneralSecurityException {
		String tmp = System.getProperty("java.io.tmpdir");
		InternalLogger.print("java.io.tmpdir: " + tmp);
		
		new BonziBot(DEBUG).start();
	}
}