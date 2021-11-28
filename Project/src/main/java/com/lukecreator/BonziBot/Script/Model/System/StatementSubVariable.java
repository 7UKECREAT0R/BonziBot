package com.lukecreator.BonziBot.Script.Model.System;

import com.lukecreator.BonziBot.CommandAPI.StringArg;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntryText;
import com.lukecreator.BonziBot.Script.Editor.StatementCategory;
import com.lukecreator.BonziBot.Script.Model.DynamicValue;
import com.lukecreator.BonziBot.Script.Model.DynamicValue.Type;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptStatement;

import net.dv8tion.jda.api.entities.Guild;

public class StatementSubVariable implements ScriptStatement {
	
	private static final long serialVersionUID = 1L;
	
	String variableName;
	DynamicValue value;
	
	@Override
	public String getKeyword() {
		return "subtract ";
	}
	
	@Override
	public String getAsCode() {
		return "subtract " + Script.asArgument(this.variableName) + " " + Script.asArgument(value.toString());
	}

	@Override
	public GuiEditEntry[] getArgs(Script caller, Guild server) {
		return new GuiEditEntry[] {
			caller.getVariableChoice(),
			new GuiEditEntryText(new StringArg("value"), null, "Value", "The value or variable which will be subtracted."),
		};
	}
	
	@Override
	public String getNewVariable() {
		return null;
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
				context.memory.operationSubConstant(this.variableName, attempt, this);
				return;
			}
		}
		context.memory.operationSubConstant(this.variableName, this.value, this);
	}
}
