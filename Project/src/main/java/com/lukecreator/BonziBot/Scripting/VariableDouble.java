package com.lukecreator.BonziBot.Scripting;

public class VariableDouble extends ScriptVariableRaw {
	
	private static final long serialVersionUID = 1L;
	
	double value;

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object o) {
		if(o instanceof Double)
			value = ((Double)o).doubleValue();
		else if(o instanceof String)
			value = Double.parseDouble((String)o);
		else if(o instanceof Integer)
			value = ((Integer)o).doubleValue();
		else
			value = -1;
		return;
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
		value += n;
	}

	@Override
	public void sub(double n) {
		value -= n;
	}

	@Override
	public void mul(double n) {
		value *= n;
	}

	@Override
	public void div(double n) {
		value /= n;
	}

	@Override
	public void concat(String other) {
		return;
	}
}
