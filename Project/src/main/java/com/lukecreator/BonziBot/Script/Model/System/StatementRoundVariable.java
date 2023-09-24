package com.lukecreator.BonziBot.Script.Model.System;

import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.Script.Editor.StatementCategory;
import com.lukecreator.BonziBot.Script.Model.DynamicValue;
import com.lukecreator.BonziBot.Script.Model.DynamicValue.Type;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptStatement;

import net.dv8tion.jda.api.entities.Guild;

public class StatementRoundVariable implements ScriptStatement {
	
	private static final long serialVersionUID = 1L;
	
	String variableName;
	
	@Override
	public String getKeyword() {
		return "round";
	}
	
	@Override
	public String getAsCode() {
		return "round " + Script.asArgument(this.variableName);
	}

	@Override
	public GuiEditEntry[] getArgs(Script caller, Guild server) {
		return new GuiEditEntry[] {
			caller.createVariableChoice(null, "Variable", "The variable to round."),
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
		this.variableName = (String)inputs[0];
	}
	
	@Override
	public void execute(ScriptContextInfo info, ScriptExecutor context) {
		DynamicValue.Type variableType = context.memory.getVariableType(this.variableName);
		
		if(variableType == Type.DECIMAL) {
			double d = context.memory.readVariableDouble(this.variableName);
			long i = (long)Math.round(d);
			context.memory.writeVariable(this.variableName, i);
		}
	}
}
