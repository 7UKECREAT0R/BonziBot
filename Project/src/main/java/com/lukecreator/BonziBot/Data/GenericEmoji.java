package com.lukecreator.BonziBot.Data;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.InternalLogger;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;

/**
 * Used in GuiButton for generalizing emotes.
 */
public class GenericEmoji implements Serializable {
	
	private static final long serialVersionUID = -7411094721697374442L;
	
	private static final JSONParser PARSER = new JSONParser(); 
	private static final String SHORTCODE_URL = "https://raw.githubusercontent.com/vdurmont/emoji-java/master/src/main/resources/emojis.json";
	private static final HashMap<String, String> SHORTCODE_TRANSLATOR = new HashMap<String, String>();
	
	/**
	 * Downloads and parses all emoji shortcodes (such as :grinning:)
	 */
	public static void initializeShortcode() {
		InternalLogger.print("Downloading emoji shortcodes...");
		try {
			String _json = BonziUtils.getStringFrom(SHORTCODE_URL);
			JSONArray json = (JSONArray)PARSER.parse(_json);
			for(Object _token: json) {
				JSONObject token = (JSONObject)_token;
				String emojiChar = (String)token.get("emojiChar");
				JSONArray aliases = (JSONArray)token.get("aliases");
				String emojiName = (String)aliases.get(0);
				SHORTCODE_TRANSLATOR.put(emojiName, emojiChar);
			}
		} catch (FileNotFoundException e) {
			InternalLogger.print("Failed shortcode download.");
			InternalLogger.printError(e);
			return;
		} catch (ParseException e) {
			InternalLogger.print("Failed shortcode parse.");
			InternalLogger.printError(e);
			return;
		}
		InternalLogger.print("Parsed " + SHORTCODE_TRANSLATOR.size() + " shortcodes.");
	}
	/**
	 * Remove extra characters surrounding shortcode (colons) and convert to lower case.
	 * @param in
	 * @return
	 */
	public static String formatShortcode(String in) {
		in = in.trim();
		if(in.startsWith(":"))
			in = in.substring(1);
		if(in.endsWith(":"))
			in = in.substring(0, in.length() - 2);
		return in.toLowerCase();
	}
	/**
	 * Convert a shortcode to its Unicode character.
	 * @param shortcode
	 * @return `null` if shortcode is invalid.
	 */
	public static String getCharacterFromShortcode(String shortcode) {
		shortcode = formatShortcode(shortcode);
		if(SHORTCODE_TRANSLATOR.containsKey(shortcode))
			return SHORTCODE_TRANSLATOR.get(shortcode);
		else
			return "";
	}
	/**
	 * Convert a shortcode to its Unicode character wrapped by a GenericEmoji.
	 * @param shortcode
	 * @return `null` if shortcode is invalid.
	 */
	public static GenericEmoji getEmojiFromShortcode(String shortcode) {
		shortcode = formatShortcode(shortcode);
		if(SHORTCODE_TRANSLATOR.containsKey(shortcode))
			return GenericEmoji.fromEmoji(SHORTCODE_TRANSLATOR.get(shortcode));
		else
			return null;
	}
	/**
	 * Returns if a string is a valid emoji shortcode.
	 * @return
	 */
	public static boolean isShortcode(String emoji) {
		emoji = emoji.trim();
		return emoji.startsWith(":") && emoji.endsWith(":") && emoji.length() > 2;
	}
	
	boolean isGeneric = false;
	boolean isGuild = false;
	String genericEmoji = null;
	long guildEmojiId = -1;
	
	public boolean getIsGeneric() {
		return this.isGeneric;
	}
	public boolean getIsGuild() {
		return this.isGuild;
	}
	public String getGenericEmoji() {
		return this.genericEmoji;
	}
	public long getGuildEmojiId() {
		return this.guildEmojiId;
	}
	public boolean isEqual(ReactionEmote re) {
		if(re.isEmote()) {
			if(this.isGeneric) return false;
			return re.getIdLong() == this.guildEmojiId;
		} else {
			if(this.isGuild) return false;
			//System.out.println(re.getEmoji() + " vs " + this.genericEmoji);
			return re.getEmoji().equals(this.genericEmoji);
		}
	}
	public void react(Message msg) {
		if(isGeneric) {
			msg.addReaction(genericEmoji).queue();
		} else {
			Emote e = EmoteCache.getEmoteById(guildEmojiId);
			msg.addReaction(e).queue();
		}
	}
	
	@Override
	public String toString() {
		if(this.isGeneric)
			return this.genericEmoji;
		else {
			Emote e = EmoteCache.getEmoteById(this.guildEmojiId);
			return (e == null) ? "invalid" : e.getAsMention();
		}
	}
	public Emoji toEmoji() {
		if(this.isGeneric)
			return Emoji.fromUnicode(this.genericEmoji);
		else return Emoji.fromEmote(EmoteCache.getEmoteById(guildEmojiId));
	}
	
	private GenericEmoji(String genericEmoji) {
		this.isGeneric = true;
		this.genericEmoji = genericEmoji;
	}
	private GenericEmoji(long guildEmojiId) {
		this.isGuild = true;
		this.guildEmojiId = guildEmojiId;
	}
	
	/**
	 * Parse an emoji. This can be a Guild emote, Emoji, or Shortcode.
	 * @param thing
	 * @return The emoji that was parsed.
	 * @throws Exception If parsing fails.
	 */
	public static GenericEmoji parseEmoji(String thing) throws Exception {
		if(isShortcode(thing)) {
			GenericEmoji ret = getEmojiFromShortcode(thing);
			if(ret == null)
				throw new Exception("Shortcode '" + thing + "' doesn't exist.");
			return ret;
		}
		
		Emoji discordEmoji = Emoji.fromMarkdown(thing);
		if(discordEmoji.isUnicode())
			if(discordEmoji.getName() == null)
				throw new Exception("Invalid markdown emoji.");
		return fromJDAEmoji(discordEmoji);
	}
	
	public static GenericEmoji fromEmote(Emote emote) {
		return new GenericEmoji(emote.getIdLong());
	}
	public static GenericEmoji fromJDAEmoji(Emoji emote) {
		if(emote.isCustom())
			return new GenericEmoji(emote.getIdLong());
		else
			return new GenericEmoji(emote.getName());
	}
	public static GenericEmoji parseEmote(String emote) {
		// <:script_system:901183464123039754>
		String[] parts = emote.split(":");
		String _id = parts[2];
		_id = _id.substring(0, _id.length() - 1);
		return new GenericEmoji(Long.parseLong(_id));
	}
	public static GenericEmoji fromReaction(ReactionEmote emote) {
		if(emote.isEmote()) {
			return new GenericEmoji(emote.getEmote().getIdLong());
		} else {
			return new GenericEmoji(emote.getEmoji());
		}
	}
	public static GenericEmoji fromEmoji(String emoji) {
		return new GenericEmoji(emoji);
	}
	public static GenericEmoji fromEmoji(int unicode) {
		char c = (char) unicode;
		return new GenericEmoji(String.valueOf(c));
	}
}