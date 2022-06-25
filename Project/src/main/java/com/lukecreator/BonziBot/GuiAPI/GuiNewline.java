package com.lukecreator.BonziBot.GuiAPI;

import net.dv8tion.jda.api.interactions.components.ItemComponent;

/**
 * Explicitly tells the GUI compiler to place a newline.
 * @author Lukec
 *
 */
public class GuiNewline extends GuiElement {

	public GuiNewline() {}
	
	@Override
	public ItemComponent toDiscord(boolean enabled) {
		return null;
	}

}
