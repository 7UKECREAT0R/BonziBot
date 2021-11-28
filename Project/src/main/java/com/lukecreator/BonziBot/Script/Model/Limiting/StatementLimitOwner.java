package com.lukecreator.BonziBot.Script.Model.Limiting;

import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.Script.Editor.StatementCategory;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptError;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptStatement;

import net.dv8tion.jda.api.entities.Guild;

public class StatementLimitOwner implements ScriptStatement {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public String getKeyword() {
		return "require_owner";
	}

	@Override
	public String getAsCode() {
		return "require_owner";
	}

	@Override
	public GuiEditEntry[] getArgs(Script caller, Guild server) {
		return new GuiEditEntry[0];
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
	public void parse(Object[] inputs) {}

	@Override
	public void execute(ScriptContextInfo info, ScriptExecutor context) {
		if(!info.hasMember) {
			ScriptExecutor.raiseError(new ScriptError("There's no member to check the ownership of...", this));
			return;
		}
		
		if(!info.member.isOwner()) {
			ScriptExecutor.raiseError(new ScriptError("You must be the owner of the server to run this script.", this));
			return;
		}
	}

}
