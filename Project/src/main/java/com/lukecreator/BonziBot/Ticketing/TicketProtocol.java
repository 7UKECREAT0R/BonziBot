package com.lukecreator.BonziBot.Ticketing;

public class TicketProtocol {
	
	public static final String PROTOCOL = "_ticket";
	
	public enum Action {
		CLOSE,
		CLAIM,
		TRANSCRIPT
	}
	
	public static String createProtocol(Action action, long ticketId) {
		return PROTOCOL + ':' + action.name() + ':' + String.valueOf(ticketId);
	}
	public static Action parseAction(String text) {
		if(text.equals("CLOSE"))
			return Action.CLOSE;
		else if(text.equals("CLAIM"))
			return Action.CLAIM;
		else if(text.equals("TRANSCRIPT"))
			return Action.TRANSCRIPT;
		
		
		return null;
	}
}
