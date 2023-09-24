package com.lukecreator.BonziBot.Script.Model.Storage;

import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.Script.Editor.StatementCategory;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptStatement;

import net.dv8tion.jda.api.entities.Guild;

public class StatementClearStorage implements ScriptStatement {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public String getKeyword() {
		return "s_clear";
	}
	@Override
	public String getAsCode() {
		return "s_clear";
	}

	@Override
	public GuiEditEntry[] getArgs(Script caller, Guild server) {
		return new GuiEditEntry[] {};
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
	public void parse(Object[] inputs) {}
	
	@Override
	public void execute(ScriptContextInfo info, ScriptExecutor context) {
		context._script.owningPackage.storage.clearData();
	}
}
