package com.lukecreator.BonziBot.Script.Model.Limiting;

import java.awt.Color;

import com.lukecreator.BonziBot.CommandAPI.StringArg;
import com.lukecreator.BonziBot.GuiAPI.DropdownItem;
import com.lukecreator.BonziBot.GuiAPI.GuiDropdown;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntryChoice;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntryText;
import com.lukecreator.BonziBot.Script.Editor.StatementCategory;
import com.lukecreator.BonziBot.Script.Model.Comparison;
import com.lukecreator.BonziBot.Script.Model.DynamicValue;
import com.lukecreator.BonziBot.Script.Model.DynamicValue.Type;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptError;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptStatement;

import net.dv8tion.jda.api.entities.Guild;

public class StatementLimitCompare implements ScriptStatement {
	private static final long serialVersionUID = 1L;

	String a;
	Comparison comparison;
	DynamicValue b;
	
	String errorMessage;
	
	@Override
	public String getKeyword() {
		return "require_comparison";
	}

	@Override
	public String getAsCode() {
		return "require_comparison " + Script.asArgument(this.a) + ' ' + this.comparison.symbol +
				' ' + this.b.asArg() + ' ' + Script.asArgument(this.errorMessage);
	}

	@Override
	public GuiEditEntry[] getArgs(Script caller, Guild server) {
		GuiDropdown dropdown = new GuiDropdown("Comparison", "comp", false);
		dropdown.addItemsTransform(Comparison.values(), comp -> {
			return new DropdownItem(comp, comp.english);
		});
		
		return new GuiEditEntry[] {
			caller.createVariableChoice(null, "A", "The variable to compare."),
			new GuiEditEntryChoice(dropdown, null, "Comparison", "The comparison that needs to be satisfied."),
			new GuiEditEntryText(new StringArg("B"), null, "B", "The value or variable to compare with."),
			new GuiEditEntryText(new StringArg("error"), "💬", "Error Message", "If the comparison fails, the message to show the user. Use {brackets} to denote variables.")
		};
	}

	@Override
	public String getNewVariable() {
		return null;
	}

	@Override
	public StatementCategory getCategory() {
		return StatementCategory.LIMITING;
	}

	@Override
	public void parse(Object[] inputs) {
		this.a = (String)inputs[0];
		this.b = DynamicValue.parse((String)inputs[2]);
		this.comparison = (Comparison)inputs[1];
		this.errorMessage = (String)inputs[3];
	}

	@Override
	public void execute(ScriptContextInfo info, ScriptExecutor context) {
		// Parse A
		DynamicValue aVar = context.memory.readVariable(this.a);
		if(aVar == null) {
			ScriptExecutor.raiseError(new ScriptError("Non-existent variable as key.", this));
			return;
		}
		
		// Parse B
		DynamicValue bVar = this.b;
		if(bVar.getType() == Type.STRING) {
			DynamicValue tryRead = context.memory.readVariable(bVar.getAsString());
			if(tryRead != null)
				bVar = tryRead;
		}
		
		// Perform comparison
		double comp = DynamicValue.compare(aVar, bVar);
		final boolean satisfied;
		
		switch(this.comparison) {
		case EQUAL_TO:
			satisfied = comp == 0.0;
			break;
		case GREATER_OR_EQUAL_TO:
			satisfied = comp >= 0.0;
			break;
		case GREATER_THAN:
			satisfied = comp > 0.0;
			break;
		case LESS_OR_EQUAL_TO:
			satisfied = comp <= 0.0;
			break;
		case LESS_THAN:
			satisfied = comp < 0.0;
			break;
		case NOT_EQUAL_TO:
			satisfied = comp != 0;
			break;
		default:
			satisfied = false;
			break;
		}
		
		if(!satisfied) {
			String baseError = this.errorMessage;
			if(baseError == null)
				baseError = "generic error message";
			String msg = context.memory.replaceVariables(this.errorMessage);
			context.cancelExecution(msg, Color.red);
			return;
		}
	}
}
