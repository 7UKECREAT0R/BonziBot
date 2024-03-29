package com.lukecreator.BonziBot.Script.Model;

/**
 * Represents a generic comparison operator.
 * @author Lukec
 */
public enum Comparison {
	
	EQUAL_TO('=', "Equal To"),
	NOT_EQUAL_TO('≠', "Not Equal To"),
	LESS_THAN('<', "Less Than"),
	LESS_OR_EQUAL_TO('≤', "Less or Equal"),
	GREATER_THAN('>', "Greater Than"),
	GREATER_OR_EQUAL_TO('≥', "Greater or Equal");
	
	public final char symbol;
	public final String english;
	
	private Comparison(char symbol, String english) {
		this.symbol = symbol;
		this.english = english;
	}
	
	public static Comparison parseComparison(String str) {
		switch(str.toUpperCase().trim()) {
		case "=":
		case "==":
		case "EQU":
			return EQUAL_TO;
		case "!=":
		case "≠":
		case "NEQ":
			return NOT_EQUAL_TO;
		case "<":
		case "LSS":
			return LESS_THAN;
		case "<=":
		case "≤":
		case "LEQ":
			return LESS_OR_EQUAL_TO;
		case ">":
		case "GTR":
			return GREATER_THAN;
		case ">=":
		case "≥":
		case "GEQ":
			return GREATER_OR_EQUAL_TO;
		default:
			return EQUAL_TO;
		}
	}
}
