package com.lukecreator.BonziBot.Script.Model.System;

import java.awt.Color;

import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.Script.Editor.StatementCategory;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptStatement;

import net.dv8tion.jda.api.entities.Guild;

public class StatementStop implements ScriptStatement {
	
	private static final long serialVersionUID = 1L;

	@Override
	public String getKeyword() {
		return "stop";
	}
	@Override
	public String getAsCode() {
		return "stop";
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
		return StatementCategory.SYSTEM;
	}

	@Override
	public void parse(Object[] inputs) {
		return;
	}

	@Override
	public void execute(ScriptContextInfo info, ScriptExecutor context) {
		context.cancelExecution("Cancelled execution.", Color.orange);
	}


}
