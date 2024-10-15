package com.lukecreator.BonziBot;

import com.lukecreator.BonziBot.Data.DataSerializer;

import java.nio.file.Path;
import java.security.GeneralSecurityException;

public class App {
	public static boolean DEBUG = false;
	
	public static void main(String[] args) throws InterruptedException, GeneralSecurityException {
		String tmp = System.getProperty("java.io.tmpdir");
		InternalLogger.print("java.io.tmpdir: " + tmp);
		InternalLogger.print("i/o absolute path: " + Path.of(DataSerializer.currentPath).toAbsolutePath());

		new BonziBot(DEBUG).start();
	}
}