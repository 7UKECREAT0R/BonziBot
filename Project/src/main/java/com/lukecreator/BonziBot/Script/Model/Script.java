package com.lukecreator.BonziBot.Script.Model;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.lukecreator.BonziBot.Data.SUser;
import com.lukecreator.BonziBot.GuiAPI.DropdownItem;
import com.lukecreator.BonziBot.GuiAPI.GuiDropdown;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntryChoice;

import net.dv8tion.jda.api.entities.User;

/**
 * A script.
 * @author Lukec
 */
public class Script implements Serializable {
	
	public static final int MAX_STATEMENTS = 50;
	
	private static final long serialVersionUID = 2L;
	
	public static String asArgument(String in) {
		if(in == null)
			return "\"\"";
		if(in.contains(" "))
			return '\"' + in + '\"';
		return in;
	}
	public static String asArgument(Color in) {
		if(in == null)
			return "[0,0,0]";
		return "[" + in.getRed() + "," + in.getBlue() + "," + in.getGreen() + "]";
	}
	
	// User-facing information.
	int invocations = 0;		// Number of times this script has been run.
	public final SUser author;	// The creator of the script.
	public final long created;	// When the script was created.
	public final String name;	// The name of the script.
	
	// The package that owns this script.
	public transient ScriptPackage owningPackage;
	
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
		
		// These are set in GuiNewScript#onButtonClicked
		this.code = null;
		this.owningPackage = null;
	}
	/**
	 * Get all the variables that are present during this script's execution.
	 * @return
	 */
	public List<String> getAllVariables() {
		List<String> list = new ArrayList<String>();
		String[] eventVars = this.method.getEventVariables();
		for(String str: eventVars)
			list.add(str);
		for(ScriptStatement statement: this.code.statements) {
			String newVar = statement.getNewVariable();
			if(newVar == null || list.contains(newVar))
				continue;
			list.add(newVar);
		}
		return list;
	}
	public GuiEditEntryChoice getVariableChoice() {
		return this.createVariableChoice(null, "Variable", "The variable to be set/affected.");
	}
	public GuiEditEntryChoice createVariableChoice(@Nullable String emoji, @Nonnull String name, @Nonnull String description) {
		List<String> vars = this.getAllVariables(); // might be more than 25... (let's hope its not)
		GuiDropdown dd = new GuiDropdown("Variable", "variable", false)
			.addItemsTransform(vars, item -> {
				return new DropdownItem(item, item);
			});
		return new GuiEditEntryChoice(dd, emoji, name, description);
	}
}
