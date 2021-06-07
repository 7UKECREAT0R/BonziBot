package com.lukecreator.BonziBot.Scripting;

public class VariableInteger extends ScriptVariableRaw {
	
	private static final long serialVersionUID = 1L;
	
	int value;
	
	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object o) {
		if(o instanceof Integer)
			value = ((Integer)o).intValue();
		else if(o instanceof String)
			value = Integer.parseInt((String)o);
		else if(o instanceof Double)
			value = (int)Math.round(((Double)o).doubleValue());
		else return;
	}

	@Override
	public void increment() {
		value++;
	}

	@Override
	public void decrement() {
		value--;
	}

	@Override
	public void add(double n) {
		value += (int)n;
	}

	@Override
	public void sub(double n) {
		value -= (int)n;
	}

	@Override
	public void mul(double n) {
		value *= (int)n;
	}

	@Override
	public void div(double n) {
		value /= (int)n;
	}

	@Override
	public void concat(String other) {
		return;
	}
	
}
