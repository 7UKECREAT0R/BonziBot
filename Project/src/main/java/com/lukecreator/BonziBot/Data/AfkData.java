package com.lukecreator.BonziBot.Data;

import java.io.Serializable;

import com.lukecreator.BonziBot.TimeSpan;

public class AfkData implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private boolean afk = false;
	private String content;
	private long until;
	
	/*
	 * Returns if this user is currently AFK.
	 */
	public boolean isAfk() {
		if(!this.afk) return false;
		
		long now = System.currentTimeMillis();
		if(now > this.until) {
			this.content = null;
			this.afk = false;
			return false;
		} else return true;
	}
	
	/*
	 * Make this AfkData go afk for <length> time in milliseconds.
	 * The content is set to <content>. (duh)
	 */
	public void goAfk(String content, long length) {
		this.afk = true;
		this.content = content;
		
		long now = System.currentTimeMillis();
		this.until = now + length;
	}
	/*
	 * Turn off being AFK despite any timings.
	 */
	public void noLongerAfk() {
		this.content = null;
		this.afk = false;
	}
	
	/*
	 * Null string if the user is not AFK.
	 * The reason this user is AFK as defined by:
	 *     goAfk(<reason>, <length>)
	 */
	public String getReasonAfk() {
		if(!afk) return null;
		return this.content;
	}
	/*
	 * Returns a TimeSpan of how long it will be
	 * until the user thinks they will be back.
	 */
	public TimeSpan timeUntilBack() {
		if(!this.afk) return TimeSpan.ZERO;
		long now = System.currentTimeMillis();
		long offset = this.until - now;
		if(offset < 0) {
			this.content = null;
			this.afk = false;
			return TimeSpan.ZERO;
		}
		return TimeSpan.fromMillis(offset);
	}
	
	@Override
	public String toString() {
		return "AfkData [afk=" + afk + ", content=" + content + ", until=" + until + "]";
	}
}