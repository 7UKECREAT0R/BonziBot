package com.lukecreator.BonziBot.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A todo list.
 * @author Lukec
 */
public class TodoList implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int MAX_FOLDERS = 12;
	
	List<TodoFolder> folders = new ArrayList<TodoFolder>();
	
	public TodoFolder getFolder(int index) {
		return this.folders.get(index);
	}
	public List<TodoFolder> getFolders() {
		return this.folders;
	}
	public void setFolder(TodoFolder folder) {
		int index = -1;
		for(int i = 0; i < folders.size(); i++) {
			TodoFolder test = folders.get(i);
			if(test.folderName.equals(folder.folderName)) {
				index = i;
				break;
			}
		}
		if(index == -1)
			return;
		
		this.folders.set(index, folder);
	}
	public TodoFolder setFolder(TodoFolder folder, int index) {
		return this.folders.set(index, folder);
	}
	public void setFolders(List<TodoFolder> folders) {
		this.folders = folders;
	}
}
