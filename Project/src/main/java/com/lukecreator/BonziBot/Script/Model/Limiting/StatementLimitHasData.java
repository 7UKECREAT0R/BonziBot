package com.lukecreator.BonziBot.Script.Model.Limiting;

import java.awt.Color;

import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.Script.Editor.StatementCategory;
import com.lukecreator.BonziBot.Script.Model.DynamicValue;
import com.lukecreator.BonziBot.Script.Model.PackageStorage;
import com.lukecreator.BonziBot.Script.Model.PackageStorage.StorageEntry;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptError;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptStatement;

import net.dv8tion.jda.api.entities.Guild;

public class StatementLimitHasData implements ScriptStatement {
	private static final long serialVersionUID = 1L;

	String key;
	
	@Override
	public String getKeyword() {
		return "require_has_data";
	}

	@Override
	public String getAsCode() {
		return "require_has_data " + Script.asArgument(this.key);
	}

	@Override
	public GuiEditEntry[] getArgs(Script caller, Guild server) {
		return new GuiEditEntry[] {
			caller.createVariableChoice(null, "Key", "The key to check for data from."),
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
		this.key = (String)inputs[0];
	}

	@Override
	public void execute(ScriptContextInfo info, ScriptExecutor context) {
		DynamicValue keyVar = context.memory.readVariable(this.key);
		if(keyVar == null) {
			ScriptExecutor.raiseError(new ScriptError("Non-existent variable as key.", this));
			return;
		}
		
		Object object = keyVar.getAsObject(context.memory);
		long key = PackageStorage.toKey(object);
		StorageEntry entry = context._script.owningPackage.storage.getData(key);
		
		if(entry == null) {
			context.cancelExecution("No data under key `" + keyVar.getConcatString() + "`.", Color.red);
			return;
		}
	}
}
