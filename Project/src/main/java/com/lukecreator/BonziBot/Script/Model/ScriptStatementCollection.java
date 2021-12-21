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

	public ScriptExecutor createExecutor() {
		this.parent.invocations++;
		return new ScriptExecutor(this.parent, ScriptMemory.MAX_PER_SCRIPT);
	}
	
	private Script parent;
	int i;
	List<ScriptStatement> statements;
	
	public ScriptStatementCollection(Script parent) {
		this.i = 0;
		this.parent = parent;
		this.statements = new ArrayList<ScriptStatement>();
	}
	
	/**
	 * Get total number of statements.
	 * @return
	 */
	public int size() {
		return this.statements.size();
	}
	/**
	 * Seek to a certain location.
	 * @param i
	 */
	public ScriptStatementCollection seek(int i) {
		if(i < 0)
			i = 0;
		if(i >= this.statements.size())
			i = this.statements.size() - 1;
		
		this.i = i;
		return this;
	}
	/**
	 * Seek to a certain location relative to the current location.
	 * @param amount
	 */
	public ScriptStatementCollection seekRelative(int amount) {
		this.i += amount;
		
		if(this.i < 0)
			this.i = 0;
		if(this.i >= this.statements.size())
			this.i = this.statements.size() - 1;
		return this;
	}
	
	/**
	 * Returns true if the next {@link #next()} call is valid.
	 * @return
	 */
	public boolean hasNext() {
		return i < this.statements.size();
	}
	/**
	 * Get the next statement and increment location.
	 * @return
	 */
	public ScriptStatement next() {
		if(i < 0 || i >= this.statements.size())
			return null;
		return this.statements.get(i++);
	}
	
	public boolean isEmpty() {
		return this.statements.isEmpty();
	}
	public int getIndex() {
		return this.i;
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
	
	
	public void add(int index, ScriptStatement statement) {
		if(index >= this.statements.size() - 1)
			this.statements.add(statement);
		else
			this.statements.add(index + 1, statement);
	}
	public void add(ScriptStatement statement) {
		this.statements.add(statement);
	}
	public void remove(int index) {
		this.statements.remove(index);
	}
}
