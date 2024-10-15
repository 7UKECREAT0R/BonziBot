package com.lukecreator.BonziBot.Data;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;

/**
 * Cache of BonziBot's emotes.
 */
public class EmoteCache {
	
	// Persistent collection of stale objects
	private static final List<RichCustomEmoji> collection = new ArrayList<RichCustomEmoji>();
	
	public static long getAmount() {
		return collection.size();
	}
	
	public static void appendGuildEmotes(Guild g) {
		SnowflakeCacheView<RichCustomEmoji> emotes = g.getEmojiCache();
		emotes.forEach(collection::add);
	}
	public static void appendEmote(RichCustomEmoji e) {
		collection.add(e);
	}
	
	public static String getEmoteNameById(long id) {
		for(RichCustomEmoji e: collection)
			if(e.getIdLong() == id)
				return e.getName();
		return null;
	}
	public static RichCustomEmoji getEmoteById(long id) {
		for(RichCustomEmoji e: collection)
			if(e.getIdLong() == id)
				return e;
		return null;
	}
	public static String mentionEmoteById(long id) {
		for(RichCustomEmoji e: collection)
			if(e.getIdLong() == id)
				return e.getAsMention();
		return null;
	}
	public static String mentionEmoteByName(String name) {
		for(RichCustomEmoji e: collection)
			if(e.getName().equalsIgnoreCase(name))
				return e.getAsMention();
		return null;
	}
	public static RichCustomEmoji getEmoteByName(String name) {
		for(RichCustomEmoji e: collection)
			if(e.getName().equalsIgnoreCase(name))
				return e;
		return null;
	}
	public static long getEmoteIdByName(String name) {
		for(RichCustomEmoji e: collection)
			if(e.getName().equalsIgnoreCase(name))
				return e.getIdLong();
		return -1;
	}
}