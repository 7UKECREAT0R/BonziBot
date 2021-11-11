package com.lukecreator.BonziBot.GuiAPI;

import java.util.Collection;
import java.util.function.Function;

import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;

/**
 * A dropdown menu in a GUI. Supports single or multi-select.
 * @author Lukec
 */
public class GuiDropdown extends GuiElement {
	
	/**
	 * Set the new selected item index and apply to elements.
	 * @param selected
	 */
	public void setSelectedIndex(int select) {
		this.selectedIndexes = new int[] {select};
		this.setSelectedIndexes();
	}
	/**
	 * Set a new array of selected index(es) and apply to elements.
	 * @param selected
	 */
	public void setSelectedIndexes(int[] selected) {
		this.selectedIndexes = selected;
		this.setSelectedIndexes();
	}
	/**
	 * Just apply existing selected indexes to elements.
	 */
	public void setSelectedIndexes() {
		for(int i = 0; i < this._items.size(); i++) {
			DropdownItem item = this._items.get(i);
			item.selected = false;
			for(int check: this.selectedIndexes) {
				if(check == i) {
					item.selected = true;
					break;
				}
			}
		}
	}
	public int[] getSelectedIndexes() {
		return this.selectedIndexes;
	}
	public Object[] getSelectedObjects() {
		if(this.selectedIndexes.length < 1)
			return null;
		Object[] fetch = new Object[this.selectedIndexes.length];
		for(int i = 0; i < fetch.length; i++) {
			int index2 = this.selectedIndexes[i];
			if(index2 == -1) {
				fetch[i] = null;
				continue;
			}
			fetch[i] = this._items.get(index2).objectRef;
		}
		return fetch;
	}
	public Object getSelectedObject() {
		if(this.selectedIndexes.length < 1)
			return null;
		return this._items.get(this.selectedIndexes[0]).objectRef;
	}
	
	int[] selectedIndexes = new int[0];
	
	// Internal
	public DropdownItemCollection _items;
	public boolean multiselect;
	public String placeholder = "Select an option.";
	
	public int getMinItems() {
		return 1;
	}
	public int getMaxItems() {
		return this.multiselect ? _items.size() : 1;
	}
	
	public GuiDropdown(String placeholder, String id, boolean multiselect) {
		this._items = new DropdownItemCollection();
		this.id = id;
		this.multiselect = multiselect;
		this.placeholder = placeholder;
	}
	public GuiDropdown addItem(DropdownItem item) {
		this._items.add(item);
		return this;
	}
	public GuiDropdown addItems(DropdownItem... items) {
		for(DropdownItem item: items)
			this._items.add(item);
		return this;
	}
	public <T> GuiDropdown addItemsTransform(Collection<T> collection, Function<T, DropdownItem> transformer) {
		for(T obj: collection)
			this._items.add(transformer.apply(obj));
		return this;
	}
	public <T> GuiDropdown addItemsTransform(T[] objects, Function<T, DropdownItem> transformer) {
		for(T obj: objects)
			this._items.add(transformer.apply(obj));
		return this;
	}
	
	@Override
	public SelectionMenu toDiscord(boolean enabled) {
		return SelectionMenu.create(this.id)
			.setPlaceholder(this.placeholder)
			.setDisabled(!enabled)
			.addOptions(this._items.toDiscord())
			.setMinValues(this.getMinItems())
			.setMaxValues(this.getMaxItems())
			.build();
	}

}
