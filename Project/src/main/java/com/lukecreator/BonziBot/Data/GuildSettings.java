package com.lukecreator.BonziBot.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.channel.ChannelType;

public class GuildSettings implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public enum FilterLevel {
		
		NONE("No filter on anything. Does not scan messages.",
			"This server has no auto-filtering."),
		
		SLURS("Filter slurs which could offend a certain group. Suits most servers that allow swearing.",
			"This server auto-filters slurs, but swearing is permitted."),
		
		SWEARS("Filter just swear words and slurs. For actively moderated family friendly servers.",
			"This server is auto-filtering swear words."),
		
		SENSITIVE("Aggresive Filtering: Filter media names, urls, and text. For strict family friendly servers.",
			"This server is strictly family-friendly.");
		
		public final String
			desc,	// serversettings description
			footer; // footer in rules menu
		private FilterLevel(String desc, String footer) {
			this.desc = desc;
			this.footer = footer;
		}
	}
	
	// ------------------
	// FIELDS
	// ------------------
	
	// Misc
	public List<Integer> disabledCommands = new ArrayList<Integer>();
	public boolean quickDraw = false;
	private String prefix = Constants.DEFAULT_PREFIX;
	private Rules rules = new Rules();
	public boolean tokenScanning = true;
	public long mutedRole = 0l;
	public long starboard = 0l;
	public int starboardLimit = 3;
	
	// Filtering
	public FilterLevel filter = FilterLevel.NONE;
	public List<String> customFilter = new ArrayList<String>();
	
	// Tags
	public boolean enableTags = false;
	public boolean privateTags = false;
	
	// Bot Commands (for the botcommands modifier)
	public boolean botCommandsEnabled = true;
	public boolean levellingEnabled = true;
	public boolean loggingEnabled = false;
	public long loggingChannelCached = 0l;
	
	// Join / Leave Messages. Be aware that these won't necessarily be good values even if enabled.
	public boolean joinMessages = false;
	public boolean joinMessageIsEmbed = false;
	public long joinMessageChannel = 0l;
	public String joinMessage = null;
	
	public boolean leaveMessages = false;
	public boolean leaveMessageIsEmbed = false;
	public long leaveMessageChannel = 0l;
	public String leaveMessage = null;
	
	// Join Role
	public boolean joinRole = false;
	public long joinRoleId = 0l;
	
	// Bans
	public boolean banAppeals = false;
	public boolean banMessage = false;
	public long banAppealsChannel = 0l;
	public String banMessageString = null;
	
	// ----------
	// FIELDS
	// ----------
	
	/**
	 * Cycles the filter option. Returns the new value.
	 */
	public FilterLevel cycleFilter() {
		switch(this.filter) {
		case NONE:
			this.filter = FilterLevel.SLURS;
			break;
		case SLURS:
			this.filter = FilterLevel.SWEARS;
			break;
		case SWEARS:
			this.filter = FilterLevel.SENSITIVE;
			break;
		case SENSITIVE:
			this.filter = FilterLevel.NONE;
			break;
		default:
			break;
		}
		return this.filter;
	}
	/**
	 * Tests the message in the filter. Returns if it's good.
	 */
	public boolean testMessageInFilter(Message msg) {
		if(msg.getChannelType() == ChannelType.PRIVATE)
			return true;
		
		String text = msg.getContentStripped();
		text = BonziUtils.stripText(text);
		
		if(!this.customFilter.isEmpty()) {
			String upper = text.toUpperCase();
			String attach = null;
			if(!msg.getAttachments().isEmpty()) {
				attach = BonziUtils.stripText(msg
					.getAttachments().get(0)
					.getFileName());
			}
			
			for(String _item: this.customFilter) {
				String item = _item.toUpperCase();
				if(upper.contains(item))
					return false; // NOT GOOD
				if(attach != null && attach.contains(item))
					return false; // BAD FILE ewroujhlujkhskl
			}
		}
		
		switch(this.filter) {
		case NONE:
			return true;
		case SLURS:
			return this.testMessageSlurs(text);
		case SWEARS:
			return this.testMessageSwears(text);
		case SENSITIVE:
			return this.testMessageSensitive(msg, text);
		default:
			return true;
		}
	}
	/**
	 * Test text in the filter. Returns true if it's good.
	 * @param msg
	 * @return
	 */
	public boolean testMessageInFilter(String msg) {
		
		msg = BonziUtils.stripText(msg);
		
		if(!this.customFilter.isEmpty()) {
			String upper = msg.toUpperCase();
			
			for(String _item: this.customFilter) {
				String item = _item.toUpperCase();
				if(upper.contains(item))
					return false; // NOT GOOD
			}
		}
		
		switch(this.filter) {
		case NONE:
			return true;
		case SLURS:
			return this.testMessageSlurs(msg);
		case SWEARS:
			return this.testMessageSwears(msg);
		case SENSITIVE:
			return this.testMessageSwears(msg);
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
		if(this.customFilter.contains(s))
			return false;
		this.customFilter.add(s);
		return true;
	}
	/**
	 * Returns false if the element does not exist.
	 */
	public boolean removeCustomFilter(String word) {
		String s = word.toUpperCase();
		return this.customFilter.remove(s);
	}
	public String[] getCustomFilter() {
		return (String[]) this.customFilter.toArray
			(new String[this.customFilter.size()]);
	}
	public void clearCustomFilter() {
		this.customFilter.clear();
	}
	
	public String getPrefix() {
		if(this.prefix == null)
			this.prefix = Constants.DEFAULT_PREFIX;
		return this.prefix;
	}
	public void setPrefix(String newPrefix) {
		if(newPrefix.length() > Constants.MAX_PREFIX_LENGTH)
			newPrefix = newPrefix.substring(0, Constants.MAX_PREFIX_LENGTH);
		this.prefix = newPrefix;
	}
	public Rules getRules() {
		if(this.rules == null) {
			this.rules = new Rules();
		}
		return this.rules;
	}
	public void setRules(Rules rules) {
		this.rules = rules;
	}
}