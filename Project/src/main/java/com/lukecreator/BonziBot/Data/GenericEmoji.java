package com.lukecreator.BonziBot.Data;

import java.io.Serializable;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;

/**
 * Used in GuiButton for generalizing emotes.
 */
public class GenericEmoji implements Serializable {
	
	private static final long serialVersionUID = -7411094721697374442L;
	
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
			//System.out.println(re.getEmoji() + " vs " + this.genericEmoji);
			return re.getEmoji().equals(this.genericEmoji);
		}
	}
	public void react(Message msg) {
		if(isGeneric) {
			msg.addReaction(genericEmoji).queue();
		} else {
			Emote e = EmojiCache.getEmoteById(guildEmojiId);
			msg.addReaction(e).queue();
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
	public static GenericEmoji fromEmoji(String emoji) {
		return new GenericEmoji(emoji);
	}
}