package com.lukecreator.BonziBot.Script.Model.System;

import java.awt.Color;
import java.io.Serializable;

import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.Script.Editor.StatementCategory;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptStatement;

public class StatementStop implements ScriptStatement, Serializable {
	
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
	public GuiEditEntry[] getArgs() {
		return new GuiEditEntry[0];
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
