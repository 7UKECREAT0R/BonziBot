package com.lukecreator.BonziBot.Script.Model;

public class InvocationLeave implements InvocationMethod {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public Implementation getImplementation() {
		return Implementation.LEAVE;
	}
	
	@Override
	public String[] getEventVariables() {
		return new String[] { "member" };
	}
	
	@Override
	public String getAsExplanation() {
		return "Run when a user leaves the server.";
	}
}
