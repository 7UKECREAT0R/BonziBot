package com.lukecreator.BonziBot.Script.Model;

import java.io.Serializable;

/**
 * An automatically parsed multi-type value. This is meant to be a convenient means
 * of parsing unknown user input during submission of script-statement arguments.
 * @author Lukec
 */
public class DynamicValue implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public enum Type {
		OBJREF(4),
		INT(8),
		DECIMAL(8),
		BOOLEAN(1),
		STRING(1); // variable
		
		public final int sz;
		private Type(int sz) {
			this.sz = sz;
		}
	}
	
	Type type;
	int objref;
	long i;
	double d;
	boolean b;
	String s;
	
	private DynamicValue() {}
	private DynamicValue(Type type, long i, double d, boolean b, String s) {
		this.type = type;
		this.i = i;
		this.d = d;
		this.b = b;
		this.s = s;
	}
	private DynamicValue(int objref) {
		this.type = Type.OBJREF;
		this.objref = objref;
	}
	public static DynamicValue parse(String parse) {
		if(parse == null)
			return null;
		
		DynamicValue dv = new DynamicValue();
		try {
			if(parse.equalsIgnoreCase("true") || parse.equalsIgnoreCase("false")) {
				dv.type = Type.BOOLEAN;
				dv.b = parse.equalsIgnoreCase("true");
			} else {
				dv.i = Long.parseLong(parse);
				dv.type = Type.INT;
			}
		} catch(NumberFormatException exc) {
			try {
				dv.d = Double.parseDouble(parse);
				dv.type = Type.DECIMAL;
			} catch(NumberFormatException exc2) {
				// This just a string
				dv.s = parse;
				dv.type = Type.STRING;
			}
		}
		
		return dv;
	}
	public static DynamicValue referenceObject(int index) {
		return new DynamicValue(index);
	}
	public static DynamicValue fromInt(long i) {
		return new DynamicValue(Type.INT, i, i, (i == 1), String.valueOf(i));
	}
	public static DynamicValue fromDouble(double d) {
		return new DynamicValue(Type.DECIMAL, (int)d, d, (((int)d) == 1), String.valueOf(d));
	}
	public static DynamicValue fromBoolean(boolean b) {
		return new DynamicValue(Type.BOOLEAN, b?1:0, b?1.0:0.0, b, String.valueOf(b));
	}
	public static DynamicValue fromString(String s) {
		return new DynamicValue(Type.STRING, 0, 0, false, s);
	}
	
	public Type getType() {
		return this.type;
	}
	public int getSize() {
		if(this.type == Type.STRING) {
			return this.s.getBytes(ScriptMemory.STRING_ENCODING).length + 1; // +1 for \0
		} else return this.type.sz;
	}
	
	public long getAsInt() {
		return this.i;
	}
	public double getAsDouble() {
		return this.d;
	}
	public boolean getAsBoolean() {
		return this.b;
	}
	public String getAsString() {
		return this.s;
	}
	
	public int getAsObjectIndex() {
		return this.objref;
	}
	
	public String getConcatString() {
		switch(this.type) {
		case BOOLEAN:
			return String.valueOf(this.b);
		case DECIMAL:
			return String.valueOf(this.d);
		case INT:
			return String.valueOf(this.i);
		case STRING:
			return this.s;
		case OBJREF:
			return "Object Reference " + this.objref;
		default:
			return "";
		}
	}
	@Override
	public String toString() {
		switch(this.type) {
		case BOOLEAN:
			return String.valueOf(this.b);
		case DECIMAL:
			return String.valueOf(this.d);
		case INT:
			return String.valueOf(this.i);
		case STRING:
			if(this.s.contains(" "))
				return "\"" + this.s + "\"";
			else
				return this.s;
		default:
			return "[???]";
		}
	}
}
