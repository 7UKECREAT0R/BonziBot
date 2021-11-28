package com.lukecreator.BonziBot.Script.Model.Storage;

import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.Script.Editor.StatementCategory;
import com.lukecreator.BonziBot.Script.Model.DynamicValue;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptError;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptStatement;
import com.lukecreator.BonziBot.Script.Model.ScriptStorage;

import net.dv8tion.jda.api.entities.Guild;

public class StatementRemoveStorage implements ScriptStatement {
	
	private static final long serialVersionUID = 1L;

	String key;
	@Override
	public String getKeyword() {
		return "s_remove";
	}
	@Override
	public String getAsCode() {
		return "s_remove " + Script.asArgument(this.key);
	}

	@Override
	public GuiEditEntry[] getArgs(Script caller, Guild server) {
		return new GuiEditEntry[] {
			caller.getVariableChoice(null, "Key", "The key to remove.")
		};
	}
	
	@Override
	public String getNewVariable() {
		return null;
	}

	@Override
	public StatementCategory getCategory() {
		return StatementCategory.STORAGE;
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
		long key = ScriptStorage.toKey(object);
		context._script.storage.removeData(key);
	}
}
