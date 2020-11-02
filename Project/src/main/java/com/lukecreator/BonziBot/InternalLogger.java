package com.lukecreator.BonziBot;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InternalLogger {
	
	enum Severity {
		MINOR,
		ERROR,
		FATAL,
	}
	
	static SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");
	
	public static void print(String message) {
		String time = fmt.format(new Date
			(System.currentTimeMillis()));
		System.out.println(time + " [INFO] " + message);
	}
	public static void printError(String message, Severity s) {
		String time = fmt.format(new Date
			(System.currentTimeMillis()));
		System.err.println(time + " [" + s.toString() + "] " + message);
	}
	public static void printError(Exception exc) {
		String time = fmt.format(new Date
			(System.currentTimeMillis()));
		System.out.println(time + " [ERROR] " + exc.getMessage());
		exc.printStackTrace();
	}
}
