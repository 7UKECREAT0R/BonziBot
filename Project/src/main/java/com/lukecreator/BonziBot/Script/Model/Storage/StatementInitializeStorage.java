package com.lukecreator.BonziBot.Script.Model.Storage;

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
import com.lukecreator.BonziBot.Script.Model.ScriptStorage;
import com.lukecreator.BonziBot.Script.Model.ScriptStorage.OutOfBlocksException;
import com.lukecreator.BonziBot.Script.Model.ScriptStorage.StorageException;

import net.dv8tion.jda.api.entities.Guild;

public class StatementInitializeStorage implements ScriptStatement {
	
	private static final long serialVersionUID = 1L;

	String key;
	DynamicValue value;
	
	@Override
	public String getKeyword() {
		return "storage_intitialize";
	}
	@Override
	public String getAsCode() {
		return "storage_intitialize " + Script.asArgument(this.key) + " " + Script.asArgument(value.toString());
	}

	@Override
	public GuiEditEntry[] getArgs(Script caller, Guild server) {
		return new GuiEditEntry[] {
			caller.getVariableChoice(null, "Key", "The key to initialize."),
			new GuiEditEntryText(new StringArg("value"), null, "Value", "The value/variable to initialize as. This will only be set if this key has no value.")
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
		this.value = DynamicValue.parse((String)inputs[1]);
	}
	
	@Override
	public void execute(ScriptContextInfo info, ScriptExecutor context) {
		DynamicValue toStore = this.value;
		if(toStore.getType() == Type.STRING) {
			DynamicValue attempt = context.memory.readVariable(this.value.getAsString());
			if(attempt != null) {
				toStore = attempt;
				return;
			}
		}
		
		DynamicValue keyVar = context.memory.readVariable(this.key);
		if(keyVar == null) {
			ScriptExecutor.raiseError(new ScriptError("Non-existent variable as key.", this));
			return;
		}
		
		Object object = keyVar.getAsObject(context.memory);
		Object store = toStore.getAsObject(context.memory);
		long key = ScriptStorage.toKey(object);
		
		try {
			ScriptStorage storage = context._script.storage;
			if(!storage.storage.containsKey(key))
				storage.putData(key, store);
		} catch (StorageException e) {
			ScriptExecutor.raiseError(new ScriptError("Unsupported data type given.", this));
			return;
		} catch (OutOfBlocksException e) {
			ScriptExecutor.raiseError(new ScriptError("Out of storage blocks! If more space is needed, contact a BonziBot developer.", this));
			return;
		}
	}
}