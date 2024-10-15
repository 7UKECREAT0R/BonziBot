package com.lukecreator.BonziBot.Script.Model;

import java.io.Serializable;

import com.lukecreator.BonziBot.BonziUtils;

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
	/**
	 * Compares A to B. -1 means less than, and 1 means greater. 0 means equals.
	 * @param aVar
	 * @param bVar
	 * @return
	 */
	public static double compare(DynamicValue aVar, DynamicValue bVar) {
		if(aVar.type == bVar.type)
			return compareEqualType(aVar, bVar);
		
		double valueA = aVar.getComparePower();
		double valueB = bVar.getComparePower();
		
		return valueA - valueB;
	}
	/**
	 * Compares A to B, is guaranteed that types are equal.
	 * @param aVar
	 * @param bVar
	 * @return
	 */
	public static double compareEqualType(DynamicValue aVar, DynamicValue bVar) {
		switch(aVar.type) {
		case BOOLEAN:
			return (aVar.b ? 1 : 0) - (bVar.b ? 1 : 0);
		case DECIMAL:
			return aVar.d - bVar.d;
		case INT:
			return aVar.i - bVar.i;
		case STRING:
			return aVar.s.length() - bVar.s.length();
		case OBJREF:
		default:
			return 0;
		}
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
	
	// Number methods for generic math.
	public boolean isNumber() {
		return this.type == Type.INT || this.type == Type.DECIMAL;
	}
	public double getNumber() {
		if(this.type == Type.INT)
			return (double)this.i;
		else
			return this.d;
	}
	
	/**
	 * Get this dynamic value as a generic Object
	 * @param objects The script memory incase a dereference has to happen.
	 * @return A java `Object` representing this `DynamicValue`.
	 */
	public Object getAsObject(ScriptMemory objects) {
		switch(this.type) {
		case BOOLEAN:
			return Boolean.valueOf(this.b);
		case DECIMAL:
			return Double.valueOf(this.d);
		case INT:
			return Long.valueOf(this.i);
		case OBJREF:
			return objects.getReferencedObject(this);
		case STRING:
			return this.s;
		default:
			return null;
		}
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
	public String asArg() {
		return Script.asArgument(this.getConcatString());
	}
	/**
	 * Get the weight of this variable in a comparison. A generalized numeric value representing this DynamicValue.
	 * @return
	 */
	public double getComparePower() {
		switch(this.type) {
		case BOOLEAN:
			return this.b ? 1 : 0;
		case DECIMAL:
			return this.d;
		case INT:
			return this.i;
		case STRING:
			return this.s.length();
		
		case OBJREF:
		default:
			return 0;
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
