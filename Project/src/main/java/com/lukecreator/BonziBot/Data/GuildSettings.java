package com.lukecreator.BonziBot.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;

public class GuildSettings implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public enum FilterLevel {
		NONE("No filter on anything. Does not scan messages."),
		SLURS("Filter slurs which could offend a certain group. Suits most servers that allow swearing."),
		SWEARS("Filter just swear words and slurs. For actively moderated family friendly servers."),
		SENSITIVE("Aggresive Filtering: Filter media names, urls, and text. For strict family friendly servers.");
		
		public final String desc;
		private FilterLevel(String string) {
			this.desc = string;
		}
	}
	
	// ----------
	// FIELDS
	// ----------
	
	// Filtering
	public FilterLevel filter = FilterLevel.NONE;
	public List<String> customFilter = new ArrayList<String>();
	
	// Tags
	public boolean enableTags = false;
	public boolean privateTags = false;
	
	// Bot Commands (for the botcommands modifier)
	public boolean botCommandsEnabled = true;
	
	// ----------
	// FIELDS
	// ----------
	
	/*
	 * Cycles the filter option. Returns the new value.
	 */
	public FilterLevel cycleFilter() {
		switch(filter) {
		case NONE:
			filter = FilterLevel.SLURS;
			break;
		case SLURS:
			filter = FilterLevel.SWEARS;
			break;
		case SWEARS:
			filter = FilterLevel.SENSITIVE;
			break;
		case SENSITIVE:
			filter = FilterLevel.NONE;
			break;
		default:
			break;
		}
		return filter;
	}
	/*
	 * Tests the message in the filter. Returns if it's good.
	 */
	public boolean testMessageInFilter(Message msg) {
		if(msg.getChannelType() == ChannelType.PRIVATE)
			return true;
		
		switch(filter) {
		case NONE:
			return true;
		case SLURS:
			return testMessageSlurs(msg);
		case SWEARS:
			return testMessageSwears(msg);
		case SENSITIVE:
			return testMessageSensitive(msg);
		default:
			return true;
		}
	}
	/*
	 * Tests the message in the custom filter. Returns false if the message contains a bad word.
	 */
	public boolean testMessageInCustomFilter(Message msg) {
		String text = msg.getContentStripped();
		String strip = BonziUtils.stripText(text);
		String[] words = strip.split(Constants.WHITESPACE_REGEX);
		for(String word: words)
		for(String filter: customFilter)
			if(word.equalsIgnoreCase(filter))
				return false;
		return true;
	}
	public boolean testMessageSlurs(Message msg) {
		String[] slurs = Constants.SLUR_WORDS;
		String content = BonziUtils.stripText(msg.getContentStripped());
		String[] words = content.split(Constants.WHITESPACE_REGEX);
		
		for(String slur: slurs)
		for(String word: words)
		if(slur.equalsIgnoreCase(word))
			return false;
		
		return true;
	}
	public boolean testMessageSwears(Message msg) {
		if(!testMessageSlurs(msg)) return false;
		String content = BonziUtils.stripText(msg.getContentStripped());
		for(String swearRegex: Constants.SWEAR_REGEX) {
			boolean match = Pattern.matches(swearRegex, content);
			if(match)
				return false;
		}
		return true;
	}
	public boolean testMessageSensitive(Message msg) {
		if(!testMessageSwears(msg)) return false;
		
		if(!msg.getAttachments().isEmpty()) {
			Attachment a = msg.getAttachments().get(0);
			String fileName = a.getFileName();
			for(String filePtrn: Constants.BAD_FILE_NAMES_REGEX) {
				if(Pattern.matches(filePtrn, fileName))
					return false;
			}
		}
		String content = BonziUtils.stripText(msg.getContentStripped());
		for(String swearRegex: Constants.SENSITIVE_SWEARS) {
			boolean match = Pattern.matches(swearRegex, content);
			if(match)
				return false;
		}
		return true;
	}
	
	/*
	 * Returns false if the element is already in the filter.
	 */
	public boolean addCustomFilter(String word) {
		String s = word.toUpperCase();
		if(customFilter.contains(s))
			return false;
		customFilter.add(s);
		return true;
	}
	/*
	 * Returns false if the element does not exist.
	 */
	public boolean removeCustomFilter(String word) {
		String s = word.toUpperCase();
		return customFilter.remove(s);
	}
	public String[] getCustomFilter() {
		return (String[]) customFilter.toArray
			(new String[customFilter.size()]);
	}
	public void clearCustomFilter() {
		customFilter.clear();
	}
}