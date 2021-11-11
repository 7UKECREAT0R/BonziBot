package com.lukecreator.BonziBot.Script.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * A package that holds up to 20 scripts.
 * @author Lukec
 */
public class ScriptPackage {
	
	public static final int MAX_SCRIPTS = 20;
	
	String packageName;
	boolean enabled;
	
	List<Script> scripts;
	
	public ScriptPackage(String name) {
		this.scripts = new ArrayList<Script>();
		this.packageName = name;
		this.enabled = true;
	}
	public boolean isEnabled() {
		return this.enabled;
	}
	public boolean toggleEnabled() {
		return (this.enabled = !this.enabled);
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public int size() {
		return this.scripts.size();
	}
	public Script get(int index) {
		if(index >= this.scripts.size())
			return null;
		if(index < 0)
			return null;
		return this.scripts.get(index);
	}
	
	public void addScript(Script script) {
		if(this.scripts.size() < MAX_SCRIPTS)
			this.scripts.add(script);
	}
	public void removeScriptByName(String name) {
		for(int i = 0; i < this.scripts.size(); i++) {
			if(this.scripts.get(i).name.equalsIgnoreCase(name)) {
				this.removeScript(i);
				break;
			}
		}
	}
	public void removeScript(int index) {
		if(index >= this.scripts.size())
			return;
		if(index < 0)
			return;
		this.scripts.remove(index);
	}
	
	
}
