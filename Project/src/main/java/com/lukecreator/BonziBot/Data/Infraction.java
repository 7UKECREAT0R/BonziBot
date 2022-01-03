package com.lukecreator.BonziBot.Data;

import com.lukecreator.BonziBot.TimeSpan;

/**
 * OBSOLETE
 * 
 * Represents an infraction level and an action attached to it.
 * Was going to be used for moderation manuals, but they were removed.
 * @author Lukec
 */
public class Infraction {
	
	public enum Action {
		NONE("No action."),
		MUTE("Mute user for {$t}."),
		KICK("Kick user."),
		BAN("Ban user for {$t}.");
		
		public final String text;
		private Action(String text) {
			this.text = text;
		}
	}
	
	public Action action;
	public TimeSpan time;
	
	public Infraction(Action action, TimeSpan time) {
		this.action = action;
		this.time = time;
	}
	public Infraction(Action action, long ms) {
		this.action = action;
		this.time = TimeSpan.fromMillis(ms);
	}
}
