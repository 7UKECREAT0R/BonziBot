package com.lukecreator.BonziBot.Script.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A collection of statements which can be iterated over through {@link #hasNext()} and {@link #next()}
 * @author Lukec
 */
public class ScriptStatementCollection implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public ScriptExecutor setupExecutor() {
		return new ScriptExecutor(this, ScriptMemory.MAX_PER_SCRIPT);
	}
	
	int i = 0;
	List<ScriptStatement> statements = new ArrayList<ScriptStatement>();
	
	/**
	 * Seek to a certain location.
	 * @param i
	 */
	public void seek(int i) {
		if(i < 0)
			i = 0;
		if(i >= this.statements.size())
			i = this.statements.size() - 1;
		
		this.i = i;
	}
	/**
	 * Seek to a certain location relative to the current location.
	 * @param amount
	 */
	public void seekRelative(int amount) {
		this.i += amount;
		
		if(this.i < 0)
			this.i = 0;
		if(this.i >= this.statements.size())
			this.i = this.statements.size() - 1;
	}
	
	/**
	 * Returns true if the next {@link #next()} call is valid.
	 * @return
	 */
	public boolean hasNext() {
		return this.statements.size() > i;
	}
	/**
	 * Get the next statement and increment location.
	 * @return
	 */
	public ScriptStatement next() {
		return this.statements.get(i++);
	}
	
	/**
	 * Peek at the next element in the collection.
	 * @return
	 */
	public ScriptStatement peek() {
		return this.statements.get(i);
	}
	/**
	 * Peek a certain number of elements into in the collection, from the current location.
	 * @param count
	 * @return
	 */
	public ScriptStatement[] peek(int count) {
		ScriptStatement[] arr = new ScriptStatement[count];
		for(int x = 0; x < count; x++) {
			int f = x + i;
			if(f >= this.statements.size())
				break;
			arr[x] = this.statements.get(f);
		}
		return arr;
	}
	
	
	public void add(ScriptStatement statement) {
		this.statements.add(statement);
	}
	public void remove(int index) {
		this.statements.remove(index);
	}
}
