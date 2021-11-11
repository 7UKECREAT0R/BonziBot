package com.lukecreator.BonziBot.Script.Model.Data;

import java.util.ArrayList;
import java.util.List;

import com.lukecreator.BonziBot.CommandAPI.StringArg;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntryText;
import com.lukecreator.BonziBot.Script.Editor.StatementCategory;
import com.lukecreator.BonziBot.Script.Model.DynamicValue;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptError;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptStatement;

import net.dv8tion.jda.api.entities.GuildChannel;

public class StatementChannelGetFromName implements ScriptStatement {

	public String name;
	public String dst;
	
	@Override
	public String getKeyword() {
		return "ch_getbyname";
	}

	@Override
	public String getAsCode() {
		if(this.name.contains(" ") && !this.name.startsWith("\""))
			return "ch_getbyname \"" + this.name + "\" " + dst;
		else
			return "ch_getbyname " + this.name + " " + dst;
	}

	@Override
	public GuiEditEntry[] getArgs() {
		return new GuiEditEntry[] {
			new GuiEditEntryText(new StringArg("name"), "✍️", "Name", "The name or variable to get a channel by."),
			new GuiEditEntryText(new StringArg("dst"), "📩", "Destination Variable", "The variable that the found channel will be placed in.")
		};
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
			ScriptExecutor.raiseError(new ScriptError("No server to get channel from... (what?)", this));
			return;
		}
		
		List<GuildChannel> allChannels = info.guild.getChannels();
		List<GuildChannel> found = new ArrayList<GuildChannel>();
		
		for(GuildChannel channel: allChannels) {
			if(channel.getName().equalsIgnoreCase(by))
				found.add(channel);
		}
		
		if(found.isEmpty()) {
			ScriptExecutor.raiseError(new ScriptError("No channels found named '" + by + "'", this));
			return;
		}
		
		int value = context.memory.createObjectReference(found.get(0));
		context.memory.writeExistingObjRef(this.dst, value);
	}

}
