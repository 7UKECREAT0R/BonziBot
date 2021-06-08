package com.lukecreator.BonziBot.GuiAPI;

import com.lukecreator.BonziBot.Data.GenericEmoji;

import net.dv8tion.jda.api.interactions.components.Button;

/**
 * A button in a GUI. Can have colors, be enabled/disabled, or act as a newline.
 * @author Lukec
 *
 */
public class GuiButton {
	
	public enum Color {
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
	
	boolean newline;
	public boolean isNewline() {
		return this.newline;
	}
	
	GenericEmoji icon;
	String text;
	Color color;
	String actionId;
	boolean enabled = true;
	
	public GuiButton(GenericEmoji icon, String text, Color color, String actionId) {
		if(actionId != null && actionId.length() > 40)
			actionId = actionId.substring(0, 40);
		this.actionId = actionId;
		this.color = color;
		this.text = text;
		this.icon = icon;
	}
	public GuiButton asEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}
	public GuiButton withColor(Color color) {
		this.color = color;
		return this;
	}
	public Button toDiscord() {
		return this.toDiscord(enabled);
	}
	public Button toDiscord(boolean enabled) {
		Button button = null;
		switch(color) {
		case BLUE:
			button = (text == null) ? Button.primary(actionId, icon.toEmoji()) : Button.primary(actionId, text);
			break;
		case GRAY:
			button = (text == null) ? Button.secondary(actionId, icon.toEmoji()) : Button.secondary(actionId, text);
			break;
		case GREEN:
			button = (text == null) ? Button.success(actionId, icon.toEmoji()) : Button.success(actionId, text);
			break;
		case RED:
			button = (text == null) ? Button.danger(actionId, icon.toEmoji()) : Button.danger(actionId, text);
			break;
		default:
			button = (text == null) ? Button.primary(actionId, icon.toEmoji()) : Button.primary(actionId, text);
		}
		if(!enabled)
			button = button.asDisabled();
		if(icon != null)
			button = button.withEmoji(icon.toEmoji());
		
		return button;
	}
	
	public static GuiButton singleEmoji(GenericEmoji icon, String actionId) {
		return new GuiButton(icon, null, Color.GRAY, actionId);
	}
	public static GuiButton newline() {
		GuiButton button = new GuiButton(null, null, Color.BLUE, null);
		button.newline = true;
		return button;
	}
}
