package com.lukecreator.BonziBot.Script.Model;

/**
 * Represents an error that occurred in the script.
 * @author Lukec
 *
 */
public class ScriptError {
	
	public final String message;
	public final ScriptStatement line;
	
	public ScriptError(String message, ScriptStatement line) {
		this.message = message;
		this.line = line;
	}
	
}
