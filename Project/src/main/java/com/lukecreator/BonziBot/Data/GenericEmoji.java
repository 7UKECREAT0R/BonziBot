package com.lukecreator.BonziBot.Data;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;

/*
 * Used in GuiButton for generalizing emotes.
 */
public class GenericEmoji {
	
	boolean isGeneric = false, isGuild = false;
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
			return re.getEmoji().equals(this.genericEmoji);
		}
	}
	
	private GenericEmoji(String genericEmoji) {
		this.isGeneric = true;
		this.genericEmoji = genericEmoji;
	}
	private GenericEmoji(long guildEmojiId) {
		this.isGuild = true;
		this.guildEmojiId = guildEmojiId;
	}
	
	public static GenericEmoji fromEmote(Emote emote) {
		return new GenericEmoji(emote.getIdLong());
	}
	public static GenericEmoji fromReaction(ReactionEmote emote) {
		if(emote.isEmote()) {
			return new GenericEmoji(emote.getEmote().getIdLong());
		} else {
			return new GenericEmoji(emote.getEmoji());
		}
	}
}