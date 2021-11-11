package com.lukecreator.BonziBot.Script.Model.System;

import java.io.Serializable;

import com.lukecreator.BonziBot.CommandAPI.BooleanArg;
import com.lukecreator.BonziBot.CommandAPI.StringArg;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntryText;
import com.lukecreator.BonziBot.Script.Editor.StatementCategory;
import com.lukecreator.BonziBot.Script.Model.DynamicValue;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptError;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptStatement;

public class StatementToBoolean implements ScriptStatement, Serializable {
	
	private static final long serialVersionUID = 1L;

	public String variableName;
	
	@Override
	public String getKeyword() {
		return "tobool";
	}
	@Override
	public String getAsCode() {
		return "tobool " + this.variableName;
	}

	@Override
	public GuiEditEntry[] getArgs() {
		return new GuiEditEntry[] {
			new GuiEditEntryText(new StringArg("valuename"), null, "Variable", "The variable that should be converted to a boolean value.")
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
		
		boolean parsed = false;
		
		switch(value.getType()) {
		case BOOLEAN:
			return;
		case DECIMAL:
			parsed = ((int)value.getAsDouble()) == 1;
			break;
		case INT:
			parsed = value.getAsInt() == 1;
			break;
		case STRING:
			parsed = BooleanArg.parseBoolean(value.getAsString());
			break;
		default:
			return; // no operation
		}
		
		context.memory.writeVariable(this.variableName, parsed);
	}
}
