package com.lukecreator.BonziBot.Scripting;

import java.io.Serializable;

import com.lukecreator.BonziBot.BonziUtils;

/**
 * The reason I call this a raw variable is because it doesn't support
 * any of the operations that are located in the settings menu for the
 * variables. Things such as user-specific variables need a container.
 * @author Lukec
 */
public abstract class ScriptVariableRaw implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public String regularName;
	public String hashmapName;
	
	public ScriptVariableRaw() {}
	public ScriptVariableRaw(String name) {
		this.regularName = name;
		this.hashmapName = BonziUtils.stripText(name).toUpperCase();
	}
	
	/*
	 * methods that need to be there
	 */
	
	public abstract Object getValue();
	public abstract void setValue(Object o);
	public abstract void increment();
	public abstract void decrement();
	public abstract void add(double n);
	public abstract void sub(double n);
	public abstract void mul(double n);
	public abstract void div(double n);
	public abstract void concat(String other);
}