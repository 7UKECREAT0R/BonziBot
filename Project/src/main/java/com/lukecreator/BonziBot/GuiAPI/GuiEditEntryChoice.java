package com.lukecreator.BonziBot.GuiAPI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.lukecreator.BonziBot.BonziUtils;

/**
 * A GuiEditEntry that accepts and parses text as its input.
 * @author Lukec
 *
 */
public class GuiEditEntryChoice extends GuiEditEntry {
	
	// Wrap a GuiDropdown so I dont have to
	// reimplement the entire thing here lol
	private final GuiDropdown element;
	
	@Override
	public String getActionID() {
		return this.element.id;
	}
	@Override
	public boolean valueGiven() {
		return this.element.selectedIndexes.length > 0;
	}
	@Override
	public Object getValue() {
		Object[] values = this.element.getSelectedObjects();
		if(values == null)
			return null;
		
		if(this.element.multiselect)
			return values;
		else
			return values[0];
	}
	@Override
	public String getStringValue() {
		if(this.element.multiselect) {
			int[] sel = this.element.getSelectedIndexes();
			String[] names = new String[sel.length];
			for(int i = 0; i < sel.length; i++)
				names[i] = this.element._items.get(sel[i]).text;
			
			return BonziUtils.stringJoinAnd(", ", names);
		} else {
			int[] _sel = this.element.getSelectedIndexes();
			if(_sel == null || _sel.length == 0)
				return null;
			int sel = _sel[0];
			return this.element._items.get(sel).text;
		}
	}
	
	public GuiDropdown getDropdown() {
		return this.element;
	}
	public GuiEditEntryChoice(GuiDropdown element, @Nullable String emoji, @Nonnull String name, @Nonnull String description) {
		this.element = element;
		this.emoji = emoji;
		this.title = name;
		this.description = description;
	}
}
