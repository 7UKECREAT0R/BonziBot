package com.lukecreator.BonziBot.Script.Model.Data;

import java.util.ArrayList;
import java.util.List;

import com.lukecreator.BonziBot.CommandAPI.StringArg;
import com.lukecreator.BonziBot.Data.Mention;
import com.lukecreator.BonziBot.Data.Mention.Type;
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
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

public class StatementChannelGetFromName implements ScriptStatement {
	
	private static final long serialVersionUID = 1L;
	
	public String name;
	public String dst;
	
	@Override
	public String getKeyword() {
		return "ch_getbyname";
	}

	@Override
	public String getAsCode() {
		if(this.name.contains(" ") && !this.name.startsWith("\""))
			return "ch_getbyname \"" + this.name + "\" " + this.dst;
		else
			return "ch_getbyname " + this.name + " " + this.dst;
	}

	@Override
	public GuiEditEntry[] getArgs(Script caller, Guild server) {
		return new GuiEditEntry[] {
			new GuiEditEntryText(new StringArg("name"), "✍️", "Name", "The name to get a channel by."),
			new GuiEditEntryText(new StringArg("dst"), "📩", "Destination Variable", "The variable that the found channel will be placed in.")
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
			ScriptExecutor.raiseError(new ScriptError("No server to get channel from... (what?)", this));
			return;
		}
		
		Mention mention = Mention.parse(by);
		List<GuildChannel> found = new ArrayList<GuildChannel>();
		
		if(mention == null) {
			List<GuildChannel> allChannels = info.guild.getChannels();
			
			for(GuildChannel channel: allChannels) {
				if(channel.getName().equalsIgnoreCase(by))
					found.add(channel);
			}
			
			if(found.isEmpty()) {
				ScriptExecutor.raiseError(new ScriptError("No channels found named '" + by + "'", this));
				return;
			}
		} else {
			if(mention.type != Type.CHANNEL) {
				ScriptExecutor.raiseError(new ScriptError("No channels found named '" + by + "'", this));
				return;
			}
			found.add(info.guild.getGuildChannelById(mention.id));
		}
		
		int value = context.memory.createObjectReference(found.get(0));
		context.memory.writeExistingObjRef(this.dst, value);
	}

}
