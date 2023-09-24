package com.lukecreator.BonziBot.Script.Model.Limiting;

import java.awt.Color;

import com.lukecreator.BonziBot.CommandAPI.StringArg;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntryText;
import com.lukecreator.BonziBot.Script.Editor.StatementCategory;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptError;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptStatement;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class StatementLimitUser implements ScriptStatement {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public String getKeyword() {
		return "require_user";
	}

	@Override
	public String getAsCode() {
		return "require_user";
	}
	
	String thing;

	@Override
	public GuiEditEntry[] getArgs(Script caller, Guild server) {
		return new GuiEditEntry[] {
			new GuiEditEntryText(new StringArg("user"), null, "User", "The ID/name of the user to require. The script will only proceed past this point if the user matches.")
		};
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
	public void parse(Object[] inputs) {
		this.thing = (String)inputs[0];
	}

	@Override
	public void execute(ScriptContextInfo info, ScriptExecutor context) {
		if(!info.hasMember) {
			ScriptExecutor.raiseError(new ScriptError("There's no member to check the ownership of...", this));
			return;
		}
		
		try {
			long id = Long.parseLong(this.thing);
			if(info.member.getIdLong() != id)
				context.cancelExecution("The only user that can run this script is <@" + id + '>', Color.red);
		} catch(NumberFormatException nfe) {
			User user = info.member.getUser();
			if(!user.getName().equals(this.thing))
				context.cancelExecution("The only user that can run this script is <@" + user.getId() + '>', Color.red);
		}
		
	}

}
