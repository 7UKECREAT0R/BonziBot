package com.lukecreator.BonziBot.Script.Model;

public class InvocationJoin implements InvocationMethod {
	@Override
	public Implementation getImplementation() {
		return Implementation.JOIN;
	}

	@Override
	public String[] getEventVariables() {
		return new String[] { "user", "server", "channel" };
	}
}
