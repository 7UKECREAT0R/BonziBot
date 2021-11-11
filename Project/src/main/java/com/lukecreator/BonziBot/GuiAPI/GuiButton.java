package com.lukecreator.BonziBot.GuiAPI;

import com.lukecreator.BonziBot.Data.GenericEmoji;

import net.dv8tion.jda.api.interactions.components.Button;

/**
 * A button in a GUI. Can have colors, be enabled/disabled, or act as a newline.
 * @author Lukec
 *
 */
public class GuiButton extends GuiElement {
	
	public enum ButtonColor {
		BLUE,
		GREEN,
		GRAY,
		RED
	}
	
	GenericEmoji icon;
	String text;
	ButtonColor color;
	public GuiButton(String text, ButtonColor color, String actionId) {
		if(actionId != null && actionId.length() > 40)
			actionId = actionId.substring(0, 40);
		this.id = actionId;
		this.color = color;
		this.text = text;
		this.icon = null;
	}
	public GuiButton(GenericEmoji icon, String text, ButtonColor color, String actionId) {
		if(actionId != null && actionId.length() > 40)
			actionId = actionId.substring(0, 40);
		this.id = actionId;
		this.color = color;
		this.text = text;
		this.icon = icon;
	}
	public static GuiButton singleEmoji(GenericEmoji icon, String actionId) {
		return new GuiButton(icon, null, ButtonColor.GRAY, actionId);
	}
	
	public GuiButton withColor(ButtonColor color) {
		this.color = color;
		return this;
	}
	
	@Override
	public Button toDiscord() {
		return this.toDiscord(enabled);
	}
	@Override
	public Button toDiscord(boolean enabled) {
		Button button = null;
		switch(color) {
		case BLUE:
			button = (text == null) ? Button.primary(id, icon.toEmoji()) : Button.primary(id, text);
			break;
		case GRAY:
			button = (text == null) ? Button.secondary(id, icon.toEmoji()) : Button.secondary(id, text);
			break;
		case GREEN:
			button = (text == null) ? Button.success(id, icon.toEmoji()) : Button.success(id, text);
			break;
		case RED:
			button = (text == null) ? Button.danger(id, icon.toEmoji()) : Button.danger(id, text);
			break;
		default:
			button = (text == null) ? Button.primary(id, icon.toEmoji()) : Button.primary(id, text);
		}
		if(!enabled)
			button = button.asDisabled();
		if(icon != null)
			button = button.withEmoji(icon.toEmoji());
		
		return button;
	}
}
