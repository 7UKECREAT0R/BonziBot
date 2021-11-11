package com.lukecreator.BonziBot.Script.Model;

public class InvocationPhrase implements InvocationMethod {
	
	@Override
	public Implementation getImplementation() {
		return Implementation.PHRASE;
	}
	
	String phrase;
	
	public void setPhrase(String phrase) {
		this.phrase = phrase.toUpperCase();
	}
	public String getPhrase() {
		return this.phrase;
	}
	
	public boolean isInText(String text) {
		String str = text.toUpperCase();
		return str.contains(phrase);
	}
	
	@Override
	public String[] getEventVariables() {
		return new String[] { "user", "message", "channel", "server" };
	}
}
