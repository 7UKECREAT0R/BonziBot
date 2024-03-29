package com.lukecreator.BonziBot.Script.Model.System;

import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.Script.Editor.StatementCategory;
import com.lukecreator.BonziBot.Script.Model.DynamicValue;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptError;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptStatement;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

public class StatementToString implements ScriptStatement {
	
	private static final long serialVersionUID = 1L;

	public String variableName;
	
	@Override
	public String getKeyword() {
		return "to_text";
	}
	@Override
	public String getAsCode() {
		return "to_text " + Script.asArgument(this.variableName);
	}

	@Override
	public GuiEditEntry[] getArgs(Script caller, Guild server) {
		return new GuiEditEntry[] {
			caller.createVariableChoice(null, "Variable", "The variable that should be converted to a text value.")
		};
	}
	
	@Override
	public String getNewVariable() {
		return this.variableName;
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
		
		// Dereference and try to get a string that makes sense.
		if(value.getType() == DynamicValue.Type.OBJREF) {
			Object object = context.memory.getReferencedObject(value);
			// Some basic toStrings
			String str = object.toString();
			if(object instanceof Member)
				str = ((Member)object).getAsMention();
			else if(object instanceof TextChannel)
				str = ((TextChannel)object).getAsMention();
			else if(object instanceof VoiceChannel)
				str = ((VoiceChannel)object).getAsMention();
			else if(object instanceof Role)
				str = ((Role)object).getName();
			context.memory.writeVariable(this.variableName, str);
		} else {
			String toString = value.getConcatString();
			context.memory.writeVariable(this.variableName, toString);
		}
	}
	
}
