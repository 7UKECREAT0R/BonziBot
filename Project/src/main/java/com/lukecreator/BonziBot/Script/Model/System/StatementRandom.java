package com.lukecreator.BonziBot.Script.Model.System;

import java.util.Random;

import com.lukecreator.BonziBot.CommandAPI.StringArg;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntryText;
import com.lukecreator.BonziBot.Script.Editor.StatementCategory;
import com.lukecreator.BonziBot.Script.Model.DynamicValue;
import com.lukecreator.BonziBot.Script.Model.DynamicValue.Type;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptError;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptStatement;

import net.dv8tion.jda.api.entities.Guild;

public class StatementRandom implements ScriptStatement {

	private static final long serialVersionUID = 1L;
	private static Random GENERATOR = new Random();
	
	String destination;
	DynamicValue min = DynamicValue.fromInt(0);
	DynamicValue max;
	
	@Override
	public String getKeyword() {
		return "random";
	}

	@Override
	public String getAsCode() {
		if(min != null)
			return "random " + Script.asArgument(destination) + ' ' + max.asArg() + ' ' + min.asArg();
		
		return "random " + Script.asArgument(destination) + ' ' + max.asArg();
	}

	@Override
	public GuiEditEntry[] getArgs(Script caller, Guild server) {
		return new GuiEditEntry[] {
				new GuiEditEntryText(new StringArg("dst"), null, "Destination", "The variable which will store the random number."),
			new GuiEditEntryText(new StringArg("max"), null, "Max", "The maximum the number can be."),
			new GuiEditEntryText(new StringArg("min"), null, "Min", "The minimum the number can be. Default 0.").optional()
		};
	}

	@Override
	public String getNewVariable() {
		return this.destination;
	}

	@Override
	public StatementCategory getCategory() {
		return StatementCategory.SYSTEM;
	}

	@Override
	public void parse(Object[] inputs) {
		this.destination = (String)inputs[0];
		this.max = DynamicValue.parse((String)inputs[1]);
		
		if(inputs.length > 2)
			this.min = DynamicValue.parse((String)inputs[2]);
	}

	@Override
	public void execute(ScriptContextInfo info, ScriptExecutor context) {
		DynamicValue minDeref = this.min;
		DynamicValue maxDeref = this.max;
		
		if(minDeref.getType() == Type.STRING) {
			DynamicValue attempt = context.memory.readVariable(minDeref.getAsString());
			if(attempt != null)
				minDeref = attempt;
			else {
				ScriptExecutor.raiseError(new ScriptError("Variable '" + minDeref.getAsString() + "' doesn't exist.", this));
				return;
			}
		}
		
		if(maxDeref.getType() == Type.STRING) {
			DynamicValue attempt = context.memory.readVariable(maxDeref.getAsString());
			if(attempt != null)
				maxDeref = attempt;
			else {
				ScriptExecutor.raiseError(new ScriptError("Variable '" + maxDeref.getAsString() + "' doesn't exist.", this));
				return;
			}
		}
		
		if(minDeref.getType() == Type.INT && maxDeref.getType() == Type.INT) {
			int intMin = (int)minDeref.getAsInt();
			int intMax = (int)maxDeref.getAsInt() + 1;
			if(intMin == intMax) {
				ScriptExecutor.raiseError(new ScriptError("Min and max cannot be equal.", this));
				return;
			}
			if(intMin > intMax) {
				int temp = intMin;
				intMin = intMax;
				intMax = temp;
			}
			
			int number = GENERATOR.nextInt(intMax - intMin) + intMin;
			context.memory.writeVariable(this.destination, number);
			return;
		}
		
		if(minDeref.isNumber() && maxDeref.isNumber()) {
			double valueMin = minDeref.getNumber();
			double valueMax = maxDeref.getNumber() + 1;
			if(valueMin == valueMax) {
				ScriptExecutor.raiseError(new ScriptError("Min and max cannot be equal.", this));
				return;
			}
			if(valueMin > valueMax) {
				double temp = valueMin;
				valueMin = valueMax;
				valueMax = temp;
			}
			
			double number = GENERATOR.nextDouble();
			number *= valueMax - valueMin;
			number += valueMin;
			context.memory.writeVariable(this.destination, number);
			return;
		} else {
			ScriptExecutor.raiseError(new ScriptError("One of the input values (min or max) was not a number.", this));
			return;
		}
	}
	
}
