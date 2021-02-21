package com.lukecreator.BonziBot;

import java.io.Serializable;
import java.time.LocalDate;

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
public class Warn implements Serializable {
	
	private static final long serialVersionUID = 3782497993870883783L;
	public String warner_name;
	public String reason;
	public LocalDate date;
	
	public Warn(User Warner, String Reason) {
		warner_name = Warner.getName();
		reason = Reason;
		date = LocalDate.now();
	}
}