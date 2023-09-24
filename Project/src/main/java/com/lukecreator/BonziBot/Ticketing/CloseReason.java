package com.lukecreator.BonziBot.Ticketing;

/**
 * A generic close reason.
 * @author Lukec
 */
public enum CloseReason {
	ANY("Other/Already Specified"),
	HANDLED("Has been handled adequately."),
	NO_ACTION("No action can be taken."),
	TROLL("Opened as a troll."),
	MISTAKENLY_OPENED("Not opened on purpose/misclick."),
	EMPTY("Was left empty/inactive.");
	
	public final String description;
	private CloseReason(String description) {
		this.description = description;
	}
}
