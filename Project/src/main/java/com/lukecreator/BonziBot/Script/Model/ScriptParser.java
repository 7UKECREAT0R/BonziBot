package com.lukecreator.BonziBot.Script.Model;

import java.lang.reflect.InvocationTargetException;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.InternalLogger;
import com.lukecreator.BonziBot.Gui.GuiScriptEditor;
import com.lukecreator.BonziBot.Script.Editor.EditorCategories;
import com.lukecreator.BonziBot.Script.Editor.StatementDescriptor;

/**
 * Provides utilities for parsing script input.
 * WARNING: THIS IS UNFINISHED AND PROBABLY WILL NOT BE FOR A WHILE
 * @author Lukec
 */
public class ScriptParser {
	
	public class ScriptParseException extends Exception {
		private static final long serialVersionUID = 1L;
		private final String msg;
		
		public ScriptParseException(String msg) {
			this.msg = msg;
		}
		@Override
		public String getMessage() {
			return this.msg;
		}
	}
	
	public static ScriptStatementCollection parseCode(Script parent, BonziBot bb, String... lines) throws ScriptParseException {
		ScriptStatementCollection ssc = new ScriptStatementCollection(parent);
		for(String line: lines)
			ssc.add(parseLine(line, bb));
		return ssc;
	}
	public static ScriptStatement parseLine(String line, BonziBot bb) throws ScriptParseException {
		if(line.startsWith(GuiScriptEditor.ARROW))
			line = line.substring(1);
		
		line = line.trim();
		if(line.length() < 1)
			return null;
		
		// parse arguments
		String[] _args = BonziUtils.args(line);
		String[] args = new String[_args.length - 1];
		String keyword = _args[0];
		
		for(int i = 0; i < args.length; i++)
			args[i] = _args[i + 1];
		
		// check for statement
		ScriptStatement statement = null;
		for(StatementDescriptor sd: EditorCategories.getAllPossibleStatements()) {
			String sdKeyword = sd.getKeyword();
			
			if(sdKeyword == null)
				continue;
			if(!keyword.equalsIgnoreCase(sdKeyword))
				continue;
			
			try {
				statement = sd.internalClass.getDeclaredConstructor().newInstance();
			} catch (InstantiationException e) {
				InternalLogger.printError(e);
				return null;
			} catch (IllegalAccessException e) {
				InternalLogger.printError(e);
				return null;
			} catch (IllegalArgumentException e) {
				InternalLogger.printError(e);
			} catch (InvocationTargetException e) {
				InternalLogger.printError(e);
			} catch (NoSuchMethodException e) {
				InternalLogger.printError(e);
			} catch (SecurityException e) {
				InternalLogger.printError(e);
			}
			
		}
		
		if(statement instanceof ScriptGetter)
			((ScriptGetter)statement).bonziInstance = bb;
		
		return statement;
	}
}
