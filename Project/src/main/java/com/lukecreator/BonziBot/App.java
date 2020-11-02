package com.lukecreator.BonziBot;

import javax.security.auth.login.LoginException;

public class App {
	public enum PremiumCommand {
		Nickall, // <Server Owner Only> Nicknames every user on the discord. Only server owners can use this command!
		RainbowRole, // <Server Owner Only> Usage: b:rainbowrole @role. Makes the targeted role rainbow!
		SuperPlay, // Puts your song at the front of the queue!
		Expose, // Exposes the last deleted message!
		Profilepic, // Gets the user's profile picture.
		Troll, // Troll a user with a random troll.
		Comment,
		
		Premium // Gives all special commands in a single package + all bonzibot premium perks. Can be bought with irl money, or coins.
	}
	public static void main(String[] args) throws LoginException, InterruptedException {
		new BonziBot(true).start();
	}
}