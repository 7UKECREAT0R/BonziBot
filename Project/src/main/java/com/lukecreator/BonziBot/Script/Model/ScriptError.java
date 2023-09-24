package com.lukecreator.BonziBot.Script.Model;

import com.lukecreator.BonziBot.BonziUtils;

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
	public ScriptError(Exception exception, ScriptStatement line) {
		String stackTrace = BonziUtils.stringJoinTransform("\n", item -> {
			return ((StackTraceElement)item).toString();
		}, exception.getStackTrace());
		
		this.message = exception.getMessage() + "\n```" + stackTrace + "```\n\n";
		this.line = line;
	}
}
