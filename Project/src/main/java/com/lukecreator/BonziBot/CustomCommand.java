package com.lukecreator.BonziBot;

import java.io.Serializable;
import java.time.LocalDate;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

/**
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * THIS IS LEGACY CODE FOR THE PURPOSE OF CONVERTING DATA
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
public class CustomCommand implements Serializable {
	
	static final long serialVersionUID = -1968509405887642422L;
	public static final CustomCommand NOT_FOUND = new CustomCommand("NotFound", "NotFound", null);
	//-----------------------------
	public final LocalDate created;
	public final String creator;
	public final Long creatorID;
	public final String server;
	public int uses;
	//-----------------------------
	public String command;
	public String response;
	//-----------------------------
	
	public CustomCommand(String Command, String Response, Member Creator) {
		if(Creator == null) {
			creator = "NotFound";
			server = "NotFound";
			creatorID = 0l;
		} else {
			User cr = Creator.getUser();
			creator = cr.getName();
			creatorID = cr.getIdLong();
			server = Creator.getGuild().getName();
		}
		command = Command;
		response = Response;
		created = LocalDate.now();
		uses = 0;
	}
	public void used() {
		uses++;
	}
}
