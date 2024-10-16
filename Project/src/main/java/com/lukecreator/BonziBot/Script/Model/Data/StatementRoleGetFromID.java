package com.lukecreator.BonziBot.Script.Model.Data;

import com.lukecreator.BonziBot.CommandAPI.StringArg;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntryText;
import com.lukecreator.BonziBot.Script.Editor.StatementCategory;
import com.lukecreator.BonziBot.Script.Model.DynamicValue;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptError;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptStatement;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatementRoleGetFromID implements ScriptStatement {
	
	private static final long serialVersionUID = 1L;
	
	public String id;
	public String dst;
	
	@Override
	public String getKeyword() {
		return "r_getbyid";
	}

	@Override
	public String getAsCode() {
		return "r_getbyid " + this.id + " " + dst;
	}

	@Override
	public GuiEditEntry[] getArgs(Script caller, Guild server) {
		return new GuiEditEntry[] {
			new GuiEditEntryText(new StringArg("id"), "🔢", "ID", "The ID of the role to get."),
			new GuiEditEntryText(new StringArg("dst"), "📩", "Destination Variable", "The variable that the found role will be placed in.")
		};
	}
	
	@Override
	public String getNewVariable() {
		return this.dst;
	}
	
	@Override
	public StatementCategory getCategory() {
		return StatementCategory.DATA;
	}

	@Override
	public void parse(Object[] inputs) {
		this.id = (String)inputs[0];
		this.dst = (String)inputs[1];
	}

	@Override
	public void execute(ScriptContextInfo info, ScriptExecutor context) {
		String str = this.id;
		
		DynamicValue tryRead = context.memory.readVariable(this.id);
		if(tryRead != null)
			str = tryRead.getConcatString();
		
		if(!info.hasGuild) {
			ScriptExecutor.raiseError(new ScriptError("No server to get role from... (what?)", this));
			return;
		}

		Pattern pattern = Message.MentionType.ROLE.getPattern();
		Matcher matcher = pattern.matcher(str);
		if(matcher.matches())
			str = matcher.group(1);
		
		try {
			Role gotten = info.guild.getRoleById(str);
			
			if(gotten == null) {
				ScriptExecutor.raiseError(new ScriptError("No role found with ID '" + str + "'", this));
				return;
			}
			
			int value = context.memory.createObjectReference(gotten);
			context.memory.writeExistingObjRef(this.dst, value);
		} catch(NumberFormatException exc) {
			ScriptExecutor.raiseError(new ScriptError("Couldn't parse ID '" + str + "'", this));
			return;
		}
	}

}
