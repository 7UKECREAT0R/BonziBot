package com.lukecreator.BonziBot.Script.Model;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class InvocationCommand implements InvocationMethod {
	
	private static final long serialVersionUID = 3L;
	
	@Override
	public Implementation getImplementation() {
		return Implementation.COMMAND;
	}
	
	public String commandName;
	public String commandDescription;
	public long _commandId;
	public List<String> argNames = new ArrayList<String>();
	
	@Override
	public String[] getEventVariables() {
		String[] vars = new String[this.argNames.size() + 2];
		for(int i = 0; i < this.argNames.size(); i++)
			vars[i + 2] = this.argNames.get(i);
		
		vars[0] = "member";
		vars[1] = "channel";
		
		return vars;
	}
	@Override
	public String getAsExplanation() {
		if(this.argNames == null || this.argNames.isEmpty())
			return "Run through command: /" + this.commandName;
		else
			return "Run through command: /" + this.commandName + " <" + String.join("> <", this.argNames) + ">";
	}
	public CommandData toDiscord() {
		SlashCommandData data = Commands.slash(this.commandName, this.commandDescription);
		int i = 1;
		for(String arg: this.argNames)
			data.addOption(OptionType.STRING, arg, "Argument " + (i++), true);
		return data;
	}
}
