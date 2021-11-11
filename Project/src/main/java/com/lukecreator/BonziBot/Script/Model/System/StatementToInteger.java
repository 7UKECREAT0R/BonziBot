package com.lukecreator.BonziBot.Script.Model.System;

import java.io.Serializable;

import com.lukecreator.BonziBot.CommandAPI.StringArg;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntryText;
import com.lukecreator.BonziBot.Script.Editor.StatementCategory;
import com.lukecreator.BonziBot.Script.Model.DynamicValue;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptError;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptStatement;

public class StatementToInteger implements ScriptStatement, Serializable {
	
	private static final long serialVersionUID = 1L;

	public String variableName;
	
	@Override
	public String getKeyword() {
		return "toint";
	}
	@Override
	public String getAsCode() {
		return "toint " + this.variableName;
	}

	@Override
	public GuiEditEntry[] getArgs() {
		return new GuiEditEntry[] {
			new GuiEditEntryText(new StringArg("valuename"), null, "Variable", "The variable that should be converted to an integer value.")
		};
	}

	@Override
	public StatementCategory getCategory() {
		return StatementCategory.SYSTEM;
	}

	@Override
	public void parse(Object[] inputs) {
		this.variableName = (String)inputs[0];
	}

	@Override
	public void execute(ScriptContextInfo info, ScriptExecutor context) {
		DynamicValue value = context.memory.readVariable(this.variableName);
		
		if(value == null) {
			ScriptExecutor.raiseError(new ScriptError("Variable doesn't exist.", this));
			return;
		}
		
		long parsed = 0;
		
		switch(value.getType()) {
		case BOOLEAN:
			parsed = value.getAsBoolean() ? 1 : 0;
			break;
		case DECIMAL:
			parsed = (int)value.getAsDouble();
			break;
		case INT:
			return;
		case STRING:
			try {
				parsed = Long.parseLong(value.getAsString());
			} catch(NumberFormatException nfe) {
				ScriptExecutor.raiseError(new ScriptError("Couldn't convert \"" + value.getAsString() + "\" to an integer.", this));
				return;
			}
			break;
		default:
			return; // no operation
		}
		
		context.memory.writeVariable(this.variableName, parsed);
	}
}
