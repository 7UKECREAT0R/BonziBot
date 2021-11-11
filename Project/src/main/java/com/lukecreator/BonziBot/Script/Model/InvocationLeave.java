package com.lukecreator.BonziBot.Script.Model;

public class InvocationLeave implements InvocationMethod {
	@Override
	public Implementation getImplementation() {
		return Implementation.LEAVE;
	}
	
	@Override
	public String[] getEventVariables() {
		return new String[] { "user", "server", "channel" };
	}
}
