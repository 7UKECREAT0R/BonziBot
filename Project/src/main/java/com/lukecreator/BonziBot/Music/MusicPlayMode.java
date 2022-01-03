package com.lukecreator.BonziBot.Music;

import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.GuiAPI.DropdownItem;

public enum MusicPlayMode {
	
	QUEUE(925488567759810580l, "Normal", "Play the next song in the queue."),
	LOOP(925488567650758656l, "Loop", "Play the current song repeatedly."),
	LOOP_QUEUE(925488567692709978l, "Loop Queue", "Continuously loop through the songs in the queue."),
	SHUFFLE(925488567889829978l, "Shuffle", "Pick a random song each time.");
	
	public final long emote;
	public final String name;
	public final String desc;
	
	MusicPlayMode(long emoteId, String name, String desc) {
		this.emote = emoteId;
		this.name = name;
		this.desc = desc;
	}
	public DropdownItem createDropdownItem() {
		return new DropdownItem(this, this.name)
			.withDescription(this.desc)
			.withEmoji(GenericEmoji.fromEmote(this.emote, false));
	}
}