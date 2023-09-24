package com.lukecreator.BonziBot.Script.Model;

import java.awt.Color;

import com.lukecreator.BonziBot.BonziUtils;

/**
 * Executes <code>ScriptStatementCollection</code>s and acts as a state machine.
 * @author Lukec
 *
 */
public class ScriptExecutor {
	
	private static ScriptError _lastError;
	public static void raiseError(ScriptError error) {
		_lastError = error;
	}
	private static ScriptError getLastError() {
		if(_lastError != null) {
			ScriptError error = _lastError;
			_lastError = null;
			return error;
		}
		
		return null;
	}
	
	public final Script _script;
	public final ScriptStatementCollection statements;
	public final ScriptMemory memory;
	
	// If a statement wishes to cancel.
	String cancelMessage = null;	// The message in the embed.
	Color cancelColor = Color.red;	// The color of the embed.
	boolean cancel = false;			// Whether to cancel the script.
	boolean silentCancel = false;
	
	public boolean wasCancelledSilently() {
		return this.cancel & this.silentCancel;
	}
	
	/**
	 * Cancel the running of this script with an embedded message and color.
	 * @param message
	 * @param color
	 */
	public void cancelExecution(String message, Color color) {
		this.cancel = true;
		this.cancelMessage = BonziUtils.cutOffString(message, 2000);
		this.cancelColor = color;
		this.silentCancel = false;
	}
	/**
	 * Silently cancel the running of this script.
	 */
	public void cancelExecution() {
		this.cancel = true;
		this.silentCancel = true;
	}
	
	public ScriptExecutor(Script script, int memory) throws OutOfMemoryError {
		this._script = script;
		this.statements = script.code.seek(0);
		this.memory = ScriptMemory.allocate(memory);
	}
	
	public void run(ScriptContextInfo context) {
		this.run(0, context);
	}
	public void run(int startAt, ScriptContextInfo context) {
		
		if(this.statements.size() < 1)
			return;
		
		this.statements.seek(0);
		ScriptError err = null;
		while(this.statements.hasNext()) {
			
			ScriptStatement current = this.statements.next();
			
			if(current instanceof ScriptGetter)
				((ScriptGetter)current).bonziInstance = context.bonzi;
			
			try {
				current.execute(context, this);
			} catch(Exception exc) {
				raiseError(new ScriptError(exc, current));
			} finally {
				if((err = getLastError()) != null) {
					// An error was raised.
					context.sendMessageEmbeds(BonziUtils.failureEmbed
						("An error has occurred.",
						err.message + "\n\nAt:\n```\n" + err.line.getAsCode() + "\n```"));
					return;
				}
				if(this.cancel) {
					if(!this.silentCancel) {
						context.sendMessageEmbeds(5, BonziUtils.quickEmbed
							(null, this.cancelMessage, this.cancelColor).build());
					}
					return;
				}
			}
		}
	}
}
