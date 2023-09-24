package com.lukecreator.BonziBot.Script.Model.System;

import com.lukecreator.BonziBot.CommandAPI.BooleanArg;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.Script.Editor.StatementCategory;
import com.lukecreator.BonziBot.Script.Model.DynamicValue;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptError;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptStatement;

import net.dv8tion.jda.api.entities.Guild;

public class StatementToBoolean implements ScriptStatement {
	
	private static final long serialVersionUID = 1L;

	public String variableName;
	
	@Override
	public String getKeyword() {
		return "to_boolean";
	}
	@Override
	public String getAsCode() {
		return "to_boolean " + Script.asArgument(this.variableName);
	}

	@Override
	public GuiEditEntry[] getArgs(Script caller, Guild server) {
		return new GuiEditEntry[] {
			caller.createVariableChoice(null, "Variable", "The variable that should be converted to a boolean value.")
		};
	}
	
	@Override
	public String getNewVariable() {
		return variableName;
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
