package com.lukecreator.BonziBot.CommandAPI;

import java.awt.Color;
import java.util.HashMap;

import com.lukecreator.BonziBot.TimeSpan;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

/*
 *   A wrapper for parsed arguments.
 * Used like a HashMap<String, Object>.
 */
public class CommandParsedArgs {
	
	// Argument Name, Passed Data.
	HashMap<String, Object> data;
	boolean underpopulated;
	
	public CommandParsedArgs(CommandArg[] array, boolean underpopulated) {
		this.data = new HashMap<String, Object>();
		this.underpopulated = underpopulated;
		if(array != null)
			for(CommandArg arg: array) {
				if(arg == null) break;
				this.data.put(arg.argName, arg.object);
			}
	}
	public boolean isUnderpopulated() {
		return this.underpopulated;
	}
	public boolean argSpecified(String name) {
		if(!data.containsKey(name))
			return false;
		
		Object o = data.get(name);
		return o != null;
	}
	
	// Getters (ft. poop casts)
	public Object get(String name) {
		return data.get(name);
	}
	public int getInt(String name) {
		Object o = data.get(name);
		if(o == null) return 0;
		return (int)(Integer)o;
	}
	public float getFloat(String name) {
		Object o = data.get(name);
		if(o == null) return 0.0F;
		return (float)(Float)o;
	}
	public boolean getBoolean(String name) {
		Object o = data.get(name);
		if(o == null) return false;
		return (boolean)(Boolean)o;
	}
	public String getString(String name) {
		Object o = data.get(name);
		if(o == null) return null;
		return (String)o;
	}
	public User getUser(String name) {
		Object o = data.get(name);
		if(o == null) return null;
		return (User)o;
	}
	public Role getRole(String name) {
		Object o = data.get(name);
		if(o == null) return null;
		return (Role)o;
	}
	public TimeSpan getTimeSpan(String name) {
		Object o = data.get(name);
		if(o == null) return null;
		return (TimeSpan)o;
	}
	public Color getColor(String name) {
		Object o = data.get(name);
		if(o == null) return null;
		return (Color)o;
	}
}