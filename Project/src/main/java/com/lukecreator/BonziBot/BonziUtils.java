package com.lukecreator.BonziBot;

public class BonziUtils {
	
	public static boolean isWhitespace(String s) {
		if(s.isEmpty()) return true;
		return s.chars().allMatch(Character::isWhitespace);
	}
	
}
