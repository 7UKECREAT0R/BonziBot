package com.lukecreator.BonziBot.Script.Model.System;

import com.lukecreator.BonziBot.CommandAPI.StringArg;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntryText;
import com.lukecreator.BonziBot.Script.Editor.StatementCategory;
import com.lukecreator.BonziBot.Script.Model.DynamicValue;
import com.lukecreator.BonziBot.Script.Model.DynamicValue.Type;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptStatement;

public class StatementSetVariable implements ScriptStatement {
	
	String variableName;
	DynamicValue value;
	
	@Override
	public String getKeyword() {
		return "set";
	}
	@Override
	public String getAsCode() {
		return "set " + this.variableName + " " + value.toString();
	}

	@Override
	public GuiEditEntry[] getArgs() {
		return new GuiEditEntry[] {
			new GuiEditEntryText(new StringArg("value"), null, "Value", "The value to set the variable to."),
			new GuiEditEntryText(new StringArg("valuename"), null, "Variable", "The variable to set.")
		};
	}

	@Override
	public StatementCategory getCategory() {
		return StatementCategory.SYSTEM;
	}

	@Override
	public void parse(Object[] inputs) {
		this.value = DynamicValue.parse((String)inputs[1]);
		this.variableName = (String)inputs[0];
	}
	
	@Override
	public void execute(ScriptContextInfo info, ScriptExecutor context) {
		if(this.value.getType() == Type.STRING) {
			// Dereference variable if it exists.
			DynamicValue attempt = context.memory.readVariable(this.value.getAsString());
			if(attempt != null) {
				context.memory.writeVariable(this.variableName, attempt);
				return;
			}
		}
		context.memory.writeVariable(this.variableName, this.value);
	}
}
