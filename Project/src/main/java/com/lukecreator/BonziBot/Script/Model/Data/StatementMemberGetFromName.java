package com.lukecreator.BonziBot.Script.Model.Data;

import java.util.List;

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
import net.dv8tion.jda.api.entities.Member;

public class StatementMemberGetFromName implements ScriptStatement {
	
	private static final long serialVersionUID = 1L;
	
	public String name;
	public String dst;
	
	@Override
	public String getKeyword() {
		return "m_getbyname";
	}

	@Override
	public String getAsCode() {
		if(this.name.contains(" ") && !this.name.startsWith("\""))
			return "m_getbyname \"" + this.name + "\" " + dst;
		else
			return "m_getbyname " + this.name + " " + dst;
		
	}

	@Override
	public GuiEditEntry[] getArgs(Script caller, Guild server) {
		return new GuiEditEntry[] {
			new GuiEditEntryText(new StringArg("name"), "✍️", "Name", "The name to get a member by."),
			new GuiEditEntryText(new StringArg("dst"), "📩", "Destination Variable", "The variable that the found member will be placed in.")
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
		this.name = (String)inputs[0];
		this.dst = (String)inputs[1];
	}

	@Override
	public void execute(ScriptContextInfo info, ScriptExecutor context) {
		String by = this.name;
		
		DynamicValue tryRead = context.memory.readVariable(this.name);
		if(tryRead != null)
			by = tryRead.getConcatString();
		
		if(!info.hasGuild) {
			ScriptExecutor.raiseError(new ScriptError("No server to get member from... (what?)", this));
			return;
		}
		
		List<Member> gotten = info.guild.getMembersByName(by, true);
		
		if(gotten.isEmpty()) {
			ScriptExecutor.raiseError(new ScriptError("No members found named '" + by + "'", this));
			return;
		}
		
		int value = context.memory.createObjectReference(gotten.get(0));
		context.memory.writeExistingObjRef(this.dst, value);
	}

}
