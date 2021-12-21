package com.lukecreator.BonziBot.Script.Model;

/**
 * A timer for polling timed-scripts.
 * @author Lukec
 *
 */
public class ScriptTimer {
	
	public long nextExecution;
	
	public final long guildId;
	public final long interval;
	public final String packageName;
	public final String scriptName;
	
	public ScriptTimer(long interval, long guildId, String packageName, String scriptName) {
		this.nextExecution = System.currentTimeMillis() + interval;
		this.interval = interval;
		this.guildId = guildId;
		this.packageName = packageName;
		this.scriptName = scriptName;
	}
	/**
	 * Poll to see if this script should be executed.
	 * Updates nextExecution automatically if true.
	 * @return
	 */
	public boolean poll(long ms) {
		if(ms > nextExecution) {
			do {
				nextExecution += interval;
			} while(ms > nextExecution);
			return true;
		}
		return false;
	}
}