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
	public boolean loggingEnabled = false;
	public long loggingChannelCached = -1l;
	
	// Join / Leave Messages. Be aware that these won't necessarily be good values even if enabled.
	public boolean joinMessages = false;
	public long joinMessageChannel = 0l;
	public String joinMessage = null;
	
	public boolean leaveMessages = false;
	public long leaveMessageChannel = 0l;
	public String leaveMessage = null;
	
	// Join Role
	public boolean joinRole = false;
	public long joinRoleId = 0l;
	// ----------
	// FIELDS
	// ----------
	
	/**
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
	/**
	 * Tests the message in the filter. Returns if it's good.
	 */
	public boolean testMessageInFilter(Message msg) {
		if(msg.getChannelType() == ChannelType.PRIVATE)
			return true;
		
		String text = msg.getContentStripped();
		text = BonziUtils.stripText(text);
		
		if(!customFilter.isEmpty()) {
			String upper = text.toUpperCase();
			String attach = null;
			if(!msg.getAttachments().isEmpty()) {
				attach = BonziUtils.stripText(msg
					.getAttachments().get(0)
					.getFileName());
			}
			
			for(String _item: customFilter) {
				String item = _item.toUpperCase();
				if(upper.contains(item))
					return false; // NOT GOOD
				if(attach != null && attach.contains(item))
					return false; // BAD FILE ewroujhlujkhskl
			}
		}
		
		switch(filter) {
		case NONE:
			return true;
		case SLURS:
			return testMessageSlurs(text);
		case SWEARS:
			return testMessageSwears(text);
		case SENSITIVE:
			return testMessageSensitive(msg, text);
		default:
			return true;
		}
	}
	public boolean testMessageSlurs(String strip) {
		for(Pattern slurRegex: Constants.SLUR_REGEX_COMPILED) {
			boolean match = slurRegex.matcher(strip).find();
			if(match) return false;
		}
		
		return true;
	}
	public boolean testMessageSwears(String strip) {
		for(Pattern swearRegex: Constants.SWEAR_REGEX_COMPILED) {
			boolean match = swearRegex.matcher(strip).find();
			if(match) return false;
		}
		return true;
	}
	public boolean testMessageSensitive(Message msg, String strip) {
		
		for(Pattern sensitiveRegex: Constants.SENSITIVE_REGEX_COMPILED) {
			boolean match = sensitiveRegex.matcher(strip).find();
			if(match) return false;
		}
		
		if(!msg.getAttachments().isEmpty()) {
			Attachment a = msg.getAttachments().get(0);
			String fileName = a.getFileName();
			for(Pattern filePtrn: Constants.BAD_FILE_NAMES_REGEX_COMPILED) {
				if(filePtrn.matcher(fileName).find())
					return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Returns false if the element is already in the filter.
	 */
	public boolean addCustomFilter(String word) {
		String s = word.toUpperCase();
		if(customFilter.contains(s))
			return false;
		customFilter.add(s);
		return true;
	}
	/**
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