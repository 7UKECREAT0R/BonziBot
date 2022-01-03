package com.lukecreator.BonziBot;

import java.io.Serializable;
import java.util.regex.Pattern;

public class TimeSpan implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public transient static final String REGEX_SINGLE_LETTER = "[0-9]+[A-z]";
	public transient static final Pattern SINGLE_LETTER = Pattern.compile(REGEX_SINGLE_LETTER);
	public transient static final TimeSpan ZERO = new TimeSpan(0);
	
	public enum TimestampStyle {
		SHORT_TIME("t"),
		LONG_TIME("T"),
		SHORT_DATE("d"),
		LONG_DATE("D"),
		SHORT_DATE_TIME("f"),
		LONG_DATE_TIME("F"),
		RELATIVE("R");
		
		final String style;
		TimestampStyle(String style) {
			this.style = style;
		}
	}
	
	public final long ms;
	private TimeSpan(long ms) {
		this.ms = ms;
	}
	
	/**
	 * Parse a timespan from a short string.
	 * 34s, 5m, 2d, 8h, 3mins, 2secs, 4days
	 * 
	 * RETURNS null if argument is not a valid TimeSpan.
	 */
	public static TimeSpan parseTimeSpan(String s) {
		if(SINGLE_LETTER.matcher(s).matches())
			return parseSingleLetter(s);
		else return null;
	}
	public static boolean stringCanBeParsed(String s) {
		return SINGLE_LETTER.matcher(s).matches();
	}
	private static TimeSpan parseSingleLetter(String s) {
		char letter = s.toCharArray()[s.length() - 1];
		String _number = s.substring(0, s.length() - 1);
		int number = Integer.parseInt(_number);
		
		switch(letter) {
		case 's':
			return fromSeconds(number);
		case 'm':
			return fromMinutes(number);
		case 'h':
			return fromHours(number);
		case 'd':
			return fromDays(number);
		default:
			return fromMillis(number);
		}
	}
	
	// Static Constructor Methods
	public static TimeSpan fromMillis(long ms) {
		return new TimeSpan(ms);
	}
	public static TimeSpan fromSeconds(long seconds) {
		return new TimeSpan(seconds*1000l);
	}
	public static TimeSpan fromMinutes(int minutes) {
		long secs = minutes * 60;
		return new TimeSpan(secs*1000l);
	}
	public static TimeSpan fromHours(int hours) {
		long secs = hours * 3600l;
		return new TimeSpan(secs*1000l);
	}
	public static TimeSpan fromDays(int days) {
		int hours = days * 24;
		long secs = hours * 3600l;
		return new TimeSpan(secs*1000l);
	}
	public static TimeSpan timeUntilMillis(long time) {
		return timeUntilMillis(time, System.currentTimeMillis());
	}
	public static TimeSpan timeUntilMillis(long time, long now) {
		if(time <= now) return new TimeSpan(0);
		return new TimeSpan(time - now);
	}
	
	// Getters
	public long getMillis() {
		return ms;
	}
	public long getSeconds() {
		return ms / 1000;
	}
	public long getMinutes() {
		return getSeconds() / 60;
	}
	public long getHours() {
		return getMinutes() / 60;
	}
	public long getDays() {
		return getHours() / 24;
	}
	
	/**
	 * Convert this TimeSpan to a short string.
	 * 2h, 10m, 50s
	 * @return
	 */
	public String toShortString() {
		return BonziUtils.getShortTimeStringMs(ms);
	}
	/**
	 * Convert this TimeSpan to a long string.
	 * 2 hours, 10 minutes, 50 seconds
	 * @return
	 */
	public String toLongString() {
		return BonziUtils.getLongTimeStringMs(ms);
	}
	/**
	 * Convert this TimeSpan to a markdown timestamp offset by the current time.
	 * For example, if this TimeSpan represented an hour, it would point to the
	 * current time + an hour.
	 * @return
	 */
	public String toTimeMarkdown(TimestampStyle format) {
		long time = this.ms + System.currentTimeMillis();
		return "<t:" + time + ':' + format.style + '>';
	}
	/**
	 * Shorthand for {@link #toTimeMarkdown(TimestampStyle)}.
	 * Uses <code>TimestampStyle.RELATIVE</code> for its style.
	 * @return
	 */
	public String toTimeMarkdown() {
		return this.toTimeMarkdown(TimestampStyle.RELATIVE);
	}
	@Override
	public String toString() {
		return this.toShortString();
	}
}