package com.lukecreator.BonziBot.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * An item that can be done.
 * @author Lukec
 */
public class TodoItem implements Serializable {
	private static final long serialVersionUID = 0L;
	public static final int MAX_NAME_LEN = 64;
	public static final int MAX_DESC_LEN = 512;
	
	public LocalDate created;
	public String name;
	public String comment;
	
	public TodoItem(String name, String comment) {
		
		if(name != null && name.length() > MAX_NAME_LEN)
			name = name.substring(0, MAX_NAME_LEN);
		
		this.created = LocalDate.now();
		this.name = name;
		this.comment = comment;
	}
}