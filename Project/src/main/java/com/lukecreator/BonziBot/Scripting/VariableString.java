package com.lukecreator.BonziBot.Scripting;

public class VariableString extends ScriptVariableRaw {
	
	private static final long serialVersionUID = 1L;
	
	String value;

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object o) {
		if(o instanceof String)
			value = (String)o;
		else value = o.toString();
	}

	@Override
	public void increment() {
		return;
	}

	@Override
	public void decrement() {
		return;
	}

	@Override
	public void add(double n) {
		return;
	}

	@Override
	public void sub(double n) {
		return;
	}

	@Override
	public void mul(double n) {
		return;
	}

	@Override
	public void div(double n) {
		return;
	}

	@Override
	public void concat(String other) {
		this.value += other;
	}
}
