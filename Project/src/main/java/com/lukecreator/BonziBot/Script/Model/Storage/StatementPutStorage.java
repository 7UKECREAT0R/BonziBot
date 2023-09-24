package com.lukecreator.BonziBot.Script.Model.Storage;

import com.lukecreator.BonziBot.CommandAPI.StringArg;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntryText;
import com.lukecreator.BonziBot.Script.Editor.StatementCategory;
import com.lukecreator.BonziBot.Script.Model.DynamicValue;
import com.lukecreator.BonziBot.Script.Model.DynamicValue.Type;
import com.lukecreator.BonziBot.Script.Model.PackageStorage;
import com.lukecreator.BonziBot.Script.Model.PackageStorage.OutOfBlocksException;
import com.lukecreator.BonziBot.Script.Model.PackageStorage.StorageException;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptError;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptStatement;

import net.dv8tion.jda.api.entities.Guild;

public class StatementPutStorage implements ScriptStatement {
	
	private static final long serialVersionUID = 1L;

	String key;
	DynamicValue value;
	
	@Override
	public String getKeyword() {
		return "s_put";
	}
	@Override
	public String getAsCode() {
		return "s_put " + Script.asArgument(this.key) + " " + Script.asArgument(this.value.toString());
	}

	@Override
	public GuiEditEntry[] getArgs(Script caller, Guild server) {
		return new GuiEditEntry[] {
			caller.createVariableChoice(null, "Key", "The key to use for this data. This is named a 'key' because you need it to get the data back."),
			new GuiEditEntryText(new StringArg("value"), null, "Value", "The value/variable to store.")
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
			}
		}
		
		DynamicValue keyVar = context.memory.readVariable(this.key);
		if(keyVar == null) {
			ScriptExecutor.raiseError(new ScriptError("Non-existent variable as key.", this));
			return;
		}
		
		long key = PackageStorage.toKey(keyVar.getAsObject(context.memory));
		Object object = toStore.getAsObject(context.memory);
		
		try {
			context._script.owningPackage.storage.putData(key, object);
		} catch (StorageException e) {
			ScriptExecutor.raiseError(new ScriptError("Unsupported data type given.", this));
			return;
		} catch (OutOfBlocksException e) {
			ScriptExecutor.raiseError(new ScriptError("Out of storage blocks! If more space is needed, contact a BonziBot developer.", this));
			return;
		}
	}
}
