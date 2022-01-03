package com.lukecreator.BonziBot.Data;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;

/**
 * Cache of BonziBot's emotes.
 */
public class EmoteCache {
	
	// Persistent collection of stale objects
	private static List<Emote> collection = new ArrayList<Emote>();
	
	public static void appendGuildEmotes(Guild g) {
		List<Emote> emotes = g.getEmotes();
		collection.addAll(emotes);
	}
	public static void appendEmote(Emote e) {
		collection.add(e);
	}
	
	public static String getEmoteNameById(long id) {
		for(Emote e: collection)
			if(e.getIdLong() == id)
				return e.getName();
		return null;
	}
	public static Emote getEmoteById(long id) {
		for(Emote e: collection)
			if(e.getIdLong() == id)
				return e;
		return null;
	}
	public static String mentionEmoteById(long id) {
		for(Emote e: collection)
			if(e.getIdLong() == id)
				return e.getAsMention();
		return null;
	}
	public static String mentionEmoteByName(String name) {
		for(Emote e: collection)
			if(e.getName().equalsIgnoreCase(name))
				return e.getAsMention();
		return null;
	}
	public static Emote getEmoteByName(String name) {
		for(Emote e: collection)
			if(e.getName().equalsIgnoreCase(name))
				return e;
		return null;
	}
	public static long getEmoteIdByName(String name) {
		for(Emote e: collection)
			if(e.getName().equalsIgnoreCase(name))
				return e.getIdLong();
		return -1;
	}
}