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
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class StatementLimitChannel implements ScriptStatement {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public String getKeyword() {
		return "require_channel";
	}

	@Override
	public String getAsCode() {
		return "require_channel";
	}
	
	String thing;

	@Override
	public GuiEditEntry[] getArgs(Script caller, Guild server) {
		return new GuiEditEntry[] {
			new GuiEditEntryText(new StringArg("channel"), null, "Channel", "The ID/name of the channel to require. The script will only proceed past this point if it's being run in this channel.")
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
		if(!info.hasChannel) {
			ScriptExecutor.raiseError(new ScriptError("There's no channel to check...", this));
			return;
		}
		
		try {
			long id = Long.parseLong(this.thing);
			if(info.channel.getIdLong() != id)
				context.cancelExecution("This script can only be run in the <#" + id + "> channel.", Color.red);
		} catch(NumberFormatException nfe) {
			TextChannel channel = info.channel;
			if(!channel.getName().equalsIgnoreCase(this.thing))
				context.cancelExecution("This script can only be run in the <#" + channel.getId() + "> channel.", Color.red);
		}
		
	}

}
