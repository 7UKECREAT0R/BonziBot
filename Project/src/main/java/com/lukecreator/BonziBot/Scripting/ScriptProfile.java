package com.lukecreator.BonziBot.Scripting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds all scripts for a guild. Also
 *  checks and distributes commands.
 * @author Lukec
 */
public class ScriptProfile implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public List<Script> scripts = new ArrayList<Script>();
	
}