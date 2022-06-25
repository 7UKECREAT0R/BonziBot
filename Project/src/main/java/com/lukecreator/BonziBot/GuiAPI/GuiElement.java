package com.lukecreator.BonziBot.GuiAPI;

import net.dv8tion.jda.api.interactions.components.ItemComponent;

/**
 * A base GUI element.
 * @author Lukec
 */
public abstract class GuiElement {
	
	protected String id;
	protected boolean enabled = true;
	
	public GuiElement asEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}
	public boolean idEqual(String other) {
		return this.id.equals(other);
	}
	
	/**
	 * Calls the implementation-assigned {@link #toDiscord()} method with this
	 * element's current enabled state. Not really meant to be overridden.
	 * @return
	 */
	public ItemComponent toDiscord() {
		return this.toDiscord(this.enabled);
	}
	
	/**
	 * Convert this BonziBot GUI element to a Discord Message Component.
	 * @return
	 */
	public abstract ItemComponent toDiscord(boolean enabled);
	
}
