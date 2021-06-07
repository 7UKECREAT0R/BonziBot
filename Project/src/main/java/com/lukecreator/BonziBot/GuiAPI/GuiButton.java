package com.lukecreator.BonziBot.GuiAPI;

import com.lukecreator.BonziBot.Data.GenericEmoji;

import net.dv8tion.jda.api.interactions.components.Button;

public class GuiButton {
	
	enum Color {
		BLUE,
		GREEN,
		GRAY,
		RED
	}
	
	// --- Legacy GuiButton Code ---
	/*GenericEmoji icon;
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
	}*/
	
	GenericEmoji icon;
	String text;
	Color color;
	String actionId;
	
	public GuiButton(GenericEmoji icon, String text, Color color, String actionId) {
		if(actionId.length() > 40)
			actionId = actionId.substring(0, 40);
		this.actionId = actionId;
		this.color = color;
		this.text = text;
		this.icon = icon;
	}
	public Button toDiscord() {
		return this.toDiscord(true);
	}
	public Button toDiscord(boolean enabled) {
		Button button;
		switch(color) {
		case BLUE:
			button = Button.primary(actionId, text);
			break;
		case GRAY:
			button = Button.secondary(actionId, text);
			break;
		case GREEN:
			button = Button.success(actionId, text);
			break;
		case RED:
			button = Button.danger(actionId, text);
			break;
		default:
			button = Button.primary(actionId, text);
		}
		if(!enabled)
			button = button.asDisabled();
		if(icon != null)
			button = button.withEmoji(icon.toEmoji());
		return button;
	}
}
