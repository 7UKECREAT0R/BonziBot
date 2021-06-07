package com.lukecreator.BonziBot.Scripting;

import java.io.Serializable;

/**
 * A custom command in a guild.
 * @author Lukec
 */
public class Script implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public String keyword;
	public String[] arguments;
	
}
