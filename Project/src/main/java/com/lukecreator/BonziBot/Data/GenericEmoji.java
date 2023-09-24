package com.lukecreator.BonziBot.Data;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.InternalLogger;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.Emoji.Type;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;

/**
 * Used for generalizing emotes/emojis into one object.
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
	 * Returns a set of all shortcode names.
	 * @return
	 */
	public static Set<String> getShortcodeNames() {
		return SHORTCODE_TRANSLATOR.keySet();
	}
	/**
	 * Returns a collection of all shortcode characters (utf-8 emoji).
	 * @return
	 */
	public static Collection<String> getShortcodeCharacters() {
		return SHORTCODE_TRANSLATOR.values();
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
	
	boolean isEmoji = false;
	boolean isEmote = false;
	
	String genericEmoji = null;
	
	boolean animated = false;
	long guildEmoteId = -1;
	
	public boolean getIsGeneric() {
		return this.isEmoji;
	}
	public boolean getIsGuild() {
		return this.isEmote;
	}
	public String getGenericEmoji() {
		return this.genericEmoji;
	}
	public long getGuildEmojiId() {
		return this.guildEmoteId;
	}
	public boolean isEqual(EmojiUnion re) {
		if(re.getType() == Type.CUSTOM) {
			if(this.isEmoji)
				return false;
			return (re.asCustom()).getIdLong() == this.guildEmoteId;
		} else {
			if(this.isEmote)
				return false;
			return (re.asUnicode()).getName().equals(this.genericEmoji);
		}
	}
	/**
	 * React to this message with the appropriate type.
	 * @param msg
	 */
	public void react(Message msg) {
		if(this.isEmoji) {
			msg.addReaction(this.toEmoji()).queue();
		} else {
			RichCustomEmoji e = EmoteCache.getEmoteById(this.guildEmoteId);
			msg.addReaction(e).queue();
		}
	}
	
	@Override
	public String toString() {
		if(this.isEmoji)
			return this.genericEmoji;
		else {
			RichCustomEmoji e = EmoteCache.getEmoteById(this.guildEmoteId);
			return (e == null) ? "invalid" : e.getAsMention();
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.animated ? 1231 : 1237);
		result = prime * result + ((this.genericEmoji == null) ? 0 : this.genericEmoji.hashCode());
		result = prime * result + (int) (this.guildEmoteId ^ (this.guildEmoteId >>> 32));
		result = prime * result + (this.isEmoji ? 1231 : 1237);
		result = prime * result + (this.isEmote ? 1231 : 1237);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		GenericEmoji other = (GenericEmoji) obj;
		if (this.animated != other.animated)
			return false;
		if (this.genericEmoji == null) {
			if (other.genericEmoji != null)
				return false;
		} else if (!this.genericEmoji.equals(other.genericEmoji))
			return false;
		if (this.guildEmoteId != other.guildEmoteId)
			return false;
		if (this.isEmoji != other.isEmoji)
			return false;
		if (this.isEmote != other.isEmote)
			return false;
		return true;
	}
	public Emoji toEmoji() {
		if(this.isEmoji)
			return Emoji.fromUnicode(this.genericEmoji);
		else
			return Emoji.fromCustom(EmoteCache.getEmoteById(this.guildEmoteId));
	}
	
	private GenericEmoji(String genericEmoji) {
		this.isEmoji = true;
		this.genericEmoji = genericEmoji;
	}
	private GenericEmoji(long guildEmojiId, boolean animated) {
		this.isEmote = true;
		this.guildEmoteId = guildEmojiId;
		this.animated = animated;
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
		
		Emoji discordEmoji = Emoji.fromFormatted(thing);
		
		if(discordEmoji.getType() == Type.UNICODE) {
			if(discordEmoji.getName() == null)
				throw new Exception("Invalid markdown emoji.");
		}
		
		return fromJDAEmoji(discordEmoji);
	}
	
	public static GenericEmoji fromEmote(long emoteId, boolean animated) {
		return new GenericEmoji(emoteId, animated);
	}
	public static GenericEmoji fromEmote(RichCustomEmoji emote) {
		return new GenericEmoji(emote.getIdLong(), emote.isAnimated());
	}
	public static GenericEmoji fromJDAEmoji(Emoji emote) {
		if(emote.getType() == Type.CUSTOM) {
			CustomEmoji custom = (CustomEmoji)emote;
			return new GenericEmoji(custom.getIdLong(), custom.isAnimated());
		} else {
			return new GenericEmoji(emote.getName());
		}
	}
	public static GenericEmoji parseEmote(String emote) {
		if(emote == null)
			return null;
		if(emote.length() == 0)
			return null;
		// <:script_system:901183464123039754>
		// <a:script_system:901183464123039754>
		String[] parts = emote.split(":");
		boolean animated = parts[0].equals("<a");
		String _id = parts[animated ? 3 : 2];
		_id = _id.substring(0, _id.length() - 1);
		long id = Long.parseLong(_id);
		
		return new GenericEmoji(id, animated);
	}
	public static GenericEmoji fromReaction(EmojiUnion emote) {
		if(emote.getType() == Type.CUSTOM) {
			CustomEmoji e = emote.asCustom();
			return new GenericEmoji(e.getIdLong(), e.isAnimated());
		} else {
			return new GenericEmoji(emote.asUnicode().getName());
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