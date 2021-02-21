package com.lukecreator.BonziBot.Data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A list with a limited size and will eliminate
 * the earliest item whenever it is filled.
 */
public class AllocationList<T> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	int size;
	ArrayList<T> list;
	
	public AllocationList(int size) {
		this.list = new ArrayList<T>(size);
		this.size = size;
	}
	
	/**
	 * Get the max size of this AllocationList.
	 */
	public int getSize() {
		return size;
	}
	/**
	 * The current data.
	 */
	public ArrayList<T> getArrayList() {
		return list;
	}
	/**
	 * Gets if the list is empty.
	 */
	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	public void add(T item) {
		while(list.size() > this.size)
			list.remove(0);
		list.add(item);
	}
	public T get(int index) {
		return list.get(index);
	}
	public T remove(int index) {
		return list.remove(index);
	}
}
