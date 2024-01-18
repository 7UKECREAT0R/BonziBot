package com.lukecreator.BonziBot;

import java.security.GeneralSecurityException;

public class App {
	public static boolean DEBUG = false;
	
	public static void main(String[] args) throws InterruptedException, GeneralSecurityException {
		String tmp = System.getProperty("java.io.tmpdir");
		InternalLogger.print("java.io.tmpdir: " + tmp);
		
		new BonziBot(DEBUG).start();
	}
}