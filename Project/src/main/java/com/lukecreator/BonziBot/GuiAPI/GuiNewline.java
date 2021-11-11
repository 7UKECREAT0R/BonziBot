package com.lukecreator.BonziBot.GuiAPI;

import net.dv8tion.jda.api.interactions.components.Component;

/**
 * Explicitly tells the GUI compiler to place a newline.
 * @author Lukec
 *
 */
public class GuiNewline extends GuiElement {

	public GuiNewline() {}
	
	@Override
	public Component toDiscord(boolean enabled) {
		return null;
	}

}
