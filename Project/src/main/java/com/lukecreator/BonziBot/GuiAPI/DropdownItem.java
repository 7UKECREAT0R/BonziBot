package com.lukecreator.BonziBot.GuiAPI;

import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.interactions.components.selections.SelectOption;

/**
 * An item in a {@link GuiDropdown}. Managed by {@link DropdownItemCollection}.
 * @author Lukec
 *
 */
public class DropdownItem {
	
	public Object objectRef; // Set by instantiator
	public boolean selected; // Set by GuiDropdown#setSelectedIndexes
	
	// Required
	public String text;
	public final String id;
	
	// Optional
	public String description;
	public GenericEmoji emoji;
	
	public DropdownItem(Object reference, String text, String id) {
		this.objectRef = reference;
		this.selected = false;
		this.text = text;
		this.id = id;
	}
	public DropdownItem(Object reference, String text) {
		this.objectRef = reference;
		this.selected = false;
		this.text = text;
		this.id = text.toLowerCase().replaceAll(Constants.WHITESPACE_REGEX, "");
	}
	
	public DropdownItem withText(String text) {
		this.text = text;
		return this;
	}
	public DropdownItem withDescription(String description) {
		this.description = description;
		return this;
	}
	public DropdownItem withEmoji(GenericEmoji emoji) {
		this.emoji = emoji;
		return this;
	}
	
	/**
	 * Convert this DropdownItem to a JDA supported format.
	 * Automatically selects if internally cached as selected
	 * @return
	 */
	public SelectOption toDiscord() {
		SelectOption option = SelectOption.of(this.text, this.id)
			.withDescription(this.description)
			.withDefault(this.selected);
		
		if(this.emoji != null)
			option = option.withEmoji(this.emoji.toEmoji());
		
		return option;
	}
}
