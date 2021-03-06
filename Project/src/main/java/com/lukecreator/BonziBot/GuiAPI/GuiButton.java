package com.lukecreator.BonziBot.GuiAPI;

import com.lukecreator.BonziBot.Data.GenericEmoji;

import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;

public class GuiButton {
	
	GenericEmoji icon;
	public GenericEmoji getIcon() {
		return icon;
	}
	int actionId;
	
	public GuiButton(GenericEmoji icon, int actionId) {
		this.icon = icon;
		this.actionId = actionId;
	}
	
	public boolean wasClicked(ReactionEmote emote) {
		return this.icon.isEqual(emote);
	}
	public int getActionId() {
		return this.actionId;
	}
}
