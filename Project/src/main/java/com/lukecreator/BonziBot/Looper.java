package com.lukecreator.BonziBot;

public class Looper<T> {
	
	int i, len;
	public final T[] data;
	
	@SafeVarargs
	public Looper(T...data) {
		this.len = data.length;
		this.data = data;
		this.i = 0;
	}
	
	public T get(int i) {
		return data[i];
	}
	public T peek() {
		return data[i];
	}
	public T next() {
		T object = data[i++];
		if(i >= len) i = 0;
		return object;
	}
	public T first() {
		return data[0];
	}
	public T last() {
		return data[len - 1];
	}
	
	public void reset() {
		i = 0;
	}
}
