package com.lukecreator.BonziBot.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Folder containing todo items.
 * @author Lukec
 */
public class TodoFolder implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int MAX_ITEMS = 128;
	public static final int MAX_NAME_LEN = 32;
	
	List<TodoItem> items;
	
	public String folderName;
	public String folderIcon;
	
	public TodoFolder(String name) {
		this.folderName = name.trim();
		this.folderIcon = null;
		this.items = new ArrayList<TodoItem>();
	}
	public TodoFolder(String name, String icon) {
		this.folderName = name.trim();
		this.folderIcon = icon;
		this.items = new ArrayList<TodoItem>();
	}
	public TodoItem[] getItems() {
		return (TodoItem[])items.toArray(new TodoItem[items.size()]);
	}
	public void addItem(TodoItem item) {
		this.items.add(item);
	}
	public void shiftUp() {
		if(this.items.isEmpty())
			return;
		this.items.add(this.items.remove(0));
	}
	public void complete() {
		this.items.remove(0);
	}
	public int size() {
		return items.size();
	}
	@Override
	public String toString() {
		if(this.folderIcon == null)
			return this.folderName;
		else
			return this.folderIcon + " " + this.folderName;
	}
}