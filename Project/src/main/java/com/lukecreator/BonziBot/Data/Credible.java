package com.lukecreator.BonziBot.Data;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicLong;

import net.dv8tion.jda.api.entities.User;

/**
 * Literally used to redirect blame in audit logs.
 * <br>Format:
 * 
 * <code>
 * 		[IDENTIFIER]::[USERID]::[TOKEN0|TOKEN1]
 * </code>
 * @author Lukec
 */
public class Credible {
	
	public static SecureRandom tokenGenerator = new SecureRandom();
	public static AtomicLong token1 = new AtomicLong();
	public static AtomicLong token0 = new AtomicLong();
	
	public static void cycle() {
		token1.set(token0.getAndSet(tokenGenerator.nextLong()));
	}
	
	private static final String IDENTIFIER = "credible";
	
	/**
	 * Returns if an AuditLogEntry reason is a credible string.
	 * @param reason
	 * @return
	 */
	public static boolean isCredibleString(String reason) {
		if(reason == null)
			return false;
		String[] parts = reason.split("::");
		if(parts.length != 3)
			return false;
		
		// check that the identifier is correct
		return parts[0].equals(IDENTIFIER);
		
		// validate that this is a real bonzibot credible token
		// disabled rn cus i didnt think it through
		/*
		String token = parts[2];
		if(!token.equals(token0.toString()) && !token.equals(token1.toString()))
			return false;
		*/
	}
	
	/**
	 * Create a credible string for a user ID.
	 * @param userId
	 * @return
	 */
	public static String create(long userId) {
		return IDENTIFIER + "::" + userId + "::" + token0.toString();
	}
	/**
	 * Create a credible string for a user.
	 * @param user
	 * @return
	 */
	public static String create(User user) {
		return create(user.getIdLong());
	}
	
	/**
	 * Get the ID stored in a credible string.
	 * @param reason
	 * @return -1 if string is not a valid credible string.
	 */
	public static long from(String reason) {
		if(!reason.startsWith(IDENTIFIER))
			return -1;
		String[] parts = reason.split("::");
		try {
			String chunk = parts[1]; // discard token and identifier
			return Long.parseLong(chunk);
		} catch(NumberFormatException | IndexOutOfBoundsException e) {
			return -1;
		}
	}
}
