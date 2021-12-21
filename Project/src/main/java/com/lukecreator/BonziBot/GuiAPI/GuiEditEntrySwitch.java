package com.lukecreator.BonziBot.GuiAPI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GuiEditEntrySwitch extends GuiEditEntry {

	public boolean value;
	final String id;
	
	
	@Override
	public String getActionID() {
		return this.id;
	}

	@Override
	public boolean valueGiven() {
		return true;
	}

	@Override
	public Object getValue() {
		return new Boolean(this.value);
	}

	@Override
	public String getStringValue() {
		return String.valueOf(this.value);
	}

	public GuiEditEntrySwitch(String id, boolean defaultValue, @Nullable String emoji, @Nonnull String name, @Nonnull String description) {
		this.id = id;
		this.value = defaultValue;
		this.emoji = emoji;
		this.title = name;
		this.description = description;
	}
}
