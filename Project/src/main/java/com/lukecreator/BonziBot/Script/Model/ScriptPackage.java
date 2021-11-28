package com.lukecreator.BonziBot.Script.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A package that holds up to 20 scripts.
 * @author Lukec
 */
public class ScriptPackage implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final int MAX_PACKAGES = 10; // 10 * 10 = 100 Total Scripts per-server (Stays below max guild command count)
	public static final int MAX_SCRIPTS = 10;
	public static final int MAX_LENGTH_PACKAGE_NAME = 32;
	
	private static String _pkgNameRegex = "[a-zA-Z _-]+";
	private static Pattern pkgNamePattern = Pattern.compile(_pkgNameRegex);
	public static boolean checkPackageName(String str) {
		if(str.length() > MAX_LENGTH_PACKAGE_NAME)
			return false;
		return pkgNamePattern.matcher(str).matches();
	}
	
	String packageName;
	boolean enabled;
	
	List<Script> scripts;
	
	public ScriptPackage(String name) {
		this.scripts = new ArrayList<Script>();
		this.packageName = name;
		this.enabled = true;
	}
	public List<Script> getScripts() {
		return this.scripts;
	}
	public String getName() {
		return this.packageName;
	}
	public String rename(String newName) {
		return this.packageName = newName;
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
	public Script getByName(String scriptName) {
		if(this.scripts == null)
			return null;
		for(Script script: this.scripts) {
			if(script.name.equalsIgnoreCase(scriptName))
				return script;
		}
		return null;
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
