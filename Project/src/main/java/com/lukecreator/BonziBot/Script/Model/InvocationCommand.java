package com.lukecreator.BonziBot.Script.Model;

import java.util.List;

public class InvocationCommand implements InvocationMethod {

	@Override
	public Implementation getImplementation() {
		return Implementation.COMMAND;
	}
	
	public String commandName;
	public List<String> argNames;
	
	@Override
	public String[] getEventVariables() {
		String[] vars = new String[this.argNames.size() + 3];
		for(int i = 0; i < this.argNames.size(); i++)
			vars[i + 3] = argNames.get(i);
		
		vars[0] = "sender";
		vars[1] = "channel";
		vars[2] = "server";
		
		return vars;
	}
}
