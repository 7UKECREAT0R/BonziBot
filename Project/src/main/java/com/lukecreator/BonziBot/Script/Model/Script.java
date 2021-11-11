package com.lukecreator.BonziBot.Script.Model;

import java.io.Serializable;

import com.lukecreator.BonziBot.Data.SUser;

import net.dv8tion.jda.api.entities.User;

/**
 * A script.
 * @author Lukec
 */
public class Script implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static String asArgument(String in) {
		if(in.contains(" "))
			return '\"' + in + '\"';
		return in;
	}
	
	// User-facing information.
	int invocations = 0;		// Number of times this script has been run.
	public final SUser author;	// The creator of the script.
	public final long created;	// When the script was created.
	public final String name;	// The name of the script.
	
	// Code information.
	public InvocationMethod method;
	public ScriptStatementCollection code;
	
	/**
	 * Initialize a new Script with timestamp set to <code>System.currentTimeMillis();</code>
	 * @param author
	 * @param name
	 */
	public Script(User author, String name) {
		this.author = new SUser(author);
		this.created = System.currentTimeMillis();
		this.name = name;
		this.method = null;
		this.code = null;
	}
	
}
