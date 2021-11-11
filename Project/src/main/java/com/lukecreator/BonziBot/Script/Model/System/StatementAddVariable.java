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

public class StatementAddVariable implements ScriptStatement {
	
	String variableName;
	DynamicValue value;
	
	@Override
	public String getKeyword() {
		return "vadd";
	}
	
	@Override
	public String getAsCode() {
		return "vadd " + this.variableName + " " + value.toString();
	}

	@Override
	public GuiEditEntry[] getArgs() {
		return new GuiEditEntry[] {
			new GuiEditEntryText(new StringArg("value"), null, "Value", "The value or variable which will be added."),
			new GuiEditEntryText(new StringArg("valuename"), null, "Variable", "The variable that will be modified.")
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
		if(value.getType() == Type.STRING) {
			// Dereference variable if it exists.
			DynamicValue attempt = context.memory.readVariable(value.getAsString());
			if(attempt != null) {
				context.memory.operationAddConstant(this.variableName, attempt, this);
				return;
			}
		}
		context.memory.operationAddConstant(this.variableName, this.value, this);
	}
}
