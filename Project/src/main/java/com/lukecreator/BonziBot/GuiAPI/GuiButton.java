package com.lukecreator.BonziBot.GuiAPI;

import com.lukecreator.BonziBot.Data.GenericEmoji;

import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

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
	public ItemComponent toDiscord() {
		return this.toDiscord(this.enabled);
	}
	@Override
	public ItemComponent toDiscord(boolean enabled) {
		Button button = null;
		switch(this.color) {
		case BLUE:
			button = (this.text == null) ? Button.primary(this.id, this.icon.toEmoji()) : Button.primary(this.id, this.text);
			break;
		case GRAY:
			button = (this.text == null) ? Button.secondary(this.id, this.icon.toEmoji()) : Button.secondary(this.id, this.text);
			break;
		case GREEN:
			button = (this.text == null) ? Button.success(this.id, this.icon.toEmoji()) : Button.success(this.id, this.text);
			break;
		case RED:
			button = (this.text == null) ? Button.danger(this.id, this.icon.toEmoji()) : Button.danger(this.id, this.text);
			break;
		default:
			button = (this.text == null) ? Button.primary(this.id, this.icon.toEmoji()) : Button.primary(this.id, this.text);
		}
		if(!enabled)
			button = button.asDisabled();
		if(this.icon != null)
			button = button.withEmoji(this.icon.toEmoji());
		
		return button;
	}
}
