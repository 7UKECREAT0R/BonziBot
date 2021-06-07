package com.lukecreator.BonziBot.Scripting;

import java.util.HashMap;

/**
 * Container for ScriptVariableRaw that supports
 * different options that modify how it works.
 * @author Lukec
 */
public class ScriptVariable {
	
	public final ScriptVariableRaw guild;
	public final HashMap<Long, ScriptVariableRaw> user;
	
	public ScriptVariable(ScriptVariableRaw type) {
		this.guild = type;
		this.user = new HashMap<Long, ScriptVariableRaw>();
	}
}
