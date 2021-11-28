package com.lukecreator.BonziBot.Script.Model;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.lukecreator.BonziBot.Script.Model.DynamicValue.Type;

import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

/**
 * A fixed size span of virtual memory for a script to execute with.
 * <br>
 * This is mostly for monitoring statistics on how intensive
 * the script is to run along with stopping memory hogs.
 *  
 * @apiNote Constructed through {@link #allocate(int)}
 * @author Lukec
 */
public class ScriptMemory {
	
	public static final int MAX_SYMBOL_NAME = 32;
	public static final int MAX_PER_SCRIPT = 2048; // 2KB memory
	public static final Charset STRING_ENCODING = StandardCharsets.UTF_8;
	public static final Pattern VARIABLE_INSERT = Pattern.compile("\\{([\\w ]+)\\}");
	
	class Symbol {
		DynamicValue.Type type;
		String name;
		int location;
		int size;
		
		Symbol(DynamicValue.Type type, String name, int location, int size) {
			this.type = type;
			this.name = name;
			this.location = location;
			this.size = size;
		}
	}
	
	// State
	List<Object> references = new ArrayList<Object>();
	HashMap<String, Symbol> symbols = new HashMap<String, Symbol>();
	int allocationPtr = 0;
	
	// Properties
	final int size;
	final byte[] bytes;
	
	private ScriptMemory(int size) {
		this.size = size;
		this.bytes = new byte[size];
	}
	/**
	 * Replace all the variables denoted by {curly brackets} with their values. Used in certain user-inputs.
	 * @return
	 */
	public String replaceVariables(String input) {
		Matcher matcher = VARIABLE_INSERT.matcher(input);
		while(matcher.find()) {
			String variableName = matcher.group(1);
			Symbol symbol = this.getSymbol(variableName);
			if(symbol == null)
				continue;
			
			final String replacement;
			if(symbol.type == Type.OBJREF) {
				DynamicValue value = this.readVariable(symbol);
				Object referenced = this.getReferencedObject(value);
				if(referenced instanceof GuildChannel)
					replacement = ((GuildChannel)referenced).getAsMention();
				else if(referenced instanceof Role)
					replacement = ((Role)referenced).getName();
				else if(referenced instanceof Member)
					replacement = ((Member)referenced).getAsMention();
				else
					replacement = referenced.toString();
			} else {
				DynamicValue value = this.readVariable(symbol);
				replacement = value.getConcatString();
			}
			
			input = input.replace(matcher.group(), replacement);
			continue;
		}
		
		return input;
	}
	
	/**
	 * Allocate a new ScriptMemory with a specified inital size.
	 * @param size
	 * @return A newly allocated set of script memory.
	 * @throws OutOfMemoryError when <code>size</code> is greater than {@value #MAX_PER_SCRIPT}
	 */
	public static ScriptMemory allocate(int size) throws OutOfMemoryError {
		if(size < 0)
			size = 0;
		if(size > MAX_PER_SCRIPT)
			throw new OutOfMemoryError("A script cannot use more than " + MAX_PER_SCRIPT + " bytes of memory. (Given " + size + "b)");
		
		return new ScriptMemory(size);
	}
	
	public int createObjectReference(Object obj) {
		this.references.add(obj);
		return this.references.size() - 1;
	}
	public Object getReferencedObject(DynamicValue referencer) {
		return this.getReferencedObject(referencer.getAsObjectIndex());
	}
	public Object getReferencedObject(int index) {
		return this.references.get(index);
	}
	
	// Symbol Management
	boolean hasSymbol(String name) {
		if(name.length() > MAX_SYMBOL_NAME)
			name = name.substring(0, MAX_SYMBOL_NAME);
		name = name.toUpperCase();
		return this.symbols.containsKey(name);
	}
	Symbol allocateSymbol(String name, int size, DynamicValue.Type type) {
		if(name.length() > MAX_SYMBOL_NAME)
			name = name.substring(0, MAX_SYMBOL_NAME);
		name = name.toUpperCase();
		
		if(this.symbols.containsKey(name))
			this.symbols.remove(name); // that section is now lost memory
		
		int location = this.allocationPtr;
		this.allocationPtr += size;
		Symbol created = new Symbol(type, name, location, size);
		this.symbols.put(name, created);
		return created;
	}
	Symbol getSymbol(String name) {
		if(name.length() > MAX_SYMBOL_NAME)
			name = name.substring(0, MAX_SYMBOL_NAME);
		name = name.toUpperCase();
		if(this.symbols.containsKey(name))
			return this.symbols.get(name);
		else
			return null;
	}
	void putSymbol(String name, Symbol symbol) {
		if(name.length() > MAX_SYMBOL_NAME)
			name = name.substring(0, MAX_SYMBOL_NAME);
		name = name.toUpperCase();
		this.symbols.put(name, symbol);
	}
	
	// High-Level Variables
	// With this storage method, string reassignment will not be possible without relocating symbols.
	public void writeExistingObjRef(String name, int refIndex) {
		this.writeVariable(name, Ints.toByteArray(refIndex), DynamicValue.Type.OBJREF);
	}
	public void writeVariable(String name, DynamicValue value) {
		switch(value.getType()) {
		case BOOLEAN:
			this.writeVariable(name, value.getAsBoolean());
			break;
		case DECIMAL:
			this.writeVariable(name, value.getAsDouble());
			break;
		case INT:
			this.writeVariable(name, value.getAsInt());
			break;
		case STRING:
			this.writeVariable(name, value.getAsString());
			break;
		case OBJREF:
			this.writeVariable(name, value.getAsObjectIndex());
		default:
			break;
		}
	}
	public void writeVariable(String name, long value) {
		this.writeVariable(name, Longs.toByteArray(value), DynamicValue.Type.INT);
	}
	public void writeVariable(String name, double value) {
		// jank implementation
		this.writeVariable(name, ByteBuffer.allocate(8).putDouble(value).array(), DynamicValue.Type.DECIMAL);
	}
	public void writeVariable(String name, boolean value) {
		this.writeVariable(name, new byte[] { (value?(byte)1:(byte)0) }, DynamicValue.Type.BOOLEAN);
	}
	public void writeVariable(String name, String value) {
		this.writeVariable(name, value.getBytes(STRING_ENCODING), DynamicValue.Type.STRING);
	}
	public void writeVariable(String name, byte[] bytes, DynamicValue.Type type) {
		Symbol symbol;
		if(this.hasSymbol(name)) {
			symbol = this.getSymbol(name);
			if(bytes.length != symbol.size) {
				// need to reallocate more memory
				symbol = this.allocateSymbol(name, bytes.length, type);
			}
		} else
			symbol = this.allocateSymbol(name, bytes.length, type);
		
		int loc = symbol.location;
		int in = 0;
		
		// Copy bytes into buffer.
		for(int i = loc; i < loc + bytes.length; i++) {
			if(i >= this.bytes.length)
				break;
			this.bytes[i] = bytes[in++];
		}
	}
	
	public void writeVariable(Symbol symbol, DynamicValue value) {
		switch(value.getType()) {
		case BOOLEAN:
			this.writeVariable(symbol, value.getAsBoolean());
			break;
		case DECIMAL:
			this.writeVariable(symbol, value.getAsDouble());
			break;
		case INT:
			this.writeVariable(symbol, value.getAsInt());
			break;
		case STRING:
			this.writeVariable(symbol, value.getAsString());
			break;
		case OBJREF:
			this.writeVariable(symbol, value.getAsObjectIndex());
			break;
		default:
			break;
		}
	}
	public void writeVariable(Symbol symbol, long value) {
		this.writeVariable(symbol.name, Longs.toByteArray(value), DynamicValue.Type.INT);
	}
	public void writeVariable(Symbol symbol, int objRef) {
		this.writeVariable(symbol.name, Ints.toByteArray(objRef), DynamicValue.Type.OBJREF);
	}
	public void writeVariable(Symbol symbol, double value) {
		// jank implementation
		this.writeVariable(symbol.name, ByteBuffer.allocate(8).putDouble(value).array(), DynamicValue.Type.DECIMAL);
	}
	public void writeVariable(Symbol symbol, boolean value) {
		this.writeVariable(symbol.name, new byte[] { (value?(byte)1:(byte)0) }, DynamicValue.Type.BOOLEAN);
	}
	public void writeVariable(Symbol symbol, String value) {
		this.writeVariable(symbol.name, value.getBytes(STRING_ENCODING), DynamicValue.Type.STRING);
	}
	
	public DynamicValue.Type getVariableType(String name) {
		Symbol symbol = this.getSymbol(name);
		
		if(symbol == null)
			return Type.STRING;
		
		return symbol.type;
	}
	public Object readObjectRef(String name) {
		Symbol symbol = this.getSymbol(name);
		if(symbol == null)
			return null;
		if(symbol.type != Type.OBJREF)
			return null;
		
		byte[] read = this.readSymbol(name);
		if(read.length == 0)
			return null;
		int index = Ints.fromByteArray(read);
		return this.references.get(index);
	}
	public DynamicValue readVariable(String name) {
		Symbol symbol = this.getSymbol(name);
		if(symbol == null)
			return null;
		
		switch(symbol.type) {
		case BOOLEAN:
			return DynamicValue.fromBoolean(this.readVariableBoolean(symbol));
		case DECIMAL:
			return DynamicValue.fromDouble(this.readVariableDouble(symbol));
		case INT:
			return DynamicValue.fromInt(this.readVariableInt(symbol));
		case STRING:
			return DynamicValue.fromString(this.readVariableString(symbol));
		case OBJREF:
			return DynamicValue.referenceObject(this.readVariableObjectIndex(symbol));
		default:
			return null;
		}
	}
	public DynamicValue readVariable(Symbol symbol) {
		if(symbol == null)
			return null;
		
		switch(symbol.type) {
		case BOOLEAN:
			return DynamicValue.fromBoolean(this.readVariableBoolean(symbol));
		case DECIMAL:
			return DynamicValue.fromDouble(this.readVariableDouble(symbol));
		case INT:
			return DynamicValue.fromInt(this.readVariableInt(symbol));
		case STRING:
			return DynamicValue.fromString(this.readVariableString(symbol));
		case OBJREF:
			return DynamicValue.referenceObject(this.readVariableObjectIndex(symbol));
		default:
			return null;
		}
	}
	public long readVariableInt(String name) {
		byte[] read = this.readSymbol(name);
		if(read.length == 0)
			return 0;
		else return Longs.fromByteArray(read);
	}
	public double readVariableDouble(String name) {
		byte[] read = this.readSymbol(name);
		if(read.length == 0)
			return 0;
		else {
			return ByteBuffer.wrap(read).getDouble();
		}
	}
	public boolean readVariableBoolean(String name) {
		byte[] read = this.readSymbol(name);
		if(read.length == 0)
			return false;
		else return read[0] == 1;
	}
	public String readVariableString(String name) {
		byte[] read = this.readSymbol(name);
		if(read.length == 0)
			return "VARIABLE NOT FOUND";
		else return new String(read, STRING_ENCODING);
	}
	public byte[] readSymbol(String name) {
		Symbol symbol = this.getSymbol(name);
		if(symbol == null)
			return new byte[0];
		
		int copy = 0;
		int sz = symbol.size;
		int start = symbol.location;
		
		byte[] buffer = new byte[sz];
		for(int i = start; i < start + sz; i++)
			buffer[copy++] = this.bytes[i];
		
		return buffer;
	}
	
	
	public Object readObjectRef(Symbol symbol) {
		if(symbol.type != Type.OBJREF)
			return null;
		
		byte[] read = this.readSymbol(symbol);
		if(read.length == 0)
			return null;
		int index = Ints.fromByteArray(read);
		return this.references.get(index);
	}
	public long readVariableInt(Symbol symbol) {
		byte[] read = this.readSymbol(symbol);
		if(read.length == 0)
			return 0;
		else return Longs.fromByteArray(read);
	}
	public int readVariableObjectIndex(Symbol symbol) {
		byte[] read = this.readSymbol(symbol);
		if(read.length == 0)
			return 0;
		else return Ints.fromByteArray(read);
	}
	public double readVariableDouble(Symbol symbol) {
		byte[] read = this.readSymbol(symbol);
		if(read.length == 0)
			return 0;
		else {
			return ByteBuffer.wrap(read).getDouble();
		}
	}
	public boolean readVariableBoolean(Symbol symbol) {
		byte[] read = this.readSymbol(symbol);
		if(read.length == 0)
			return false;
		else return read[0] == 1;
	}
	public String readVariableString(Symbol symbol) {
		byte[] read = this.readSymbol(symbol);
		if(read.length == 0)
			return "VARIABLE NOT FOUND";
		else return new String(read, STRING_ENCODING);
	}
	public byte[] readSymbol(Symbol symbol) {
		if(symbol == null)
			return new byte[0];
		
		int copy = 0;
		int sz = symbol.size;
		int start = symbol.location;
		
		byte[] buffer = new byte[sz];
		for(int i = start; i < start + sz; i++) {
			if(i >= this.bytes.length) {
				this.bytes[i] = 0;
				continue;
			}
			buffer[copy++] = this.bytes[i];
		}
		
		return buffer;
	}


	public void operationAddConstant(String variableName, DynamicValue value, ScriptStatement caller) {
		
		Symbol src = this.getSymbol(variableName);
		
		if(src == null) {
			ScriptExecutor.raiseError(new ScriptError("Undefined symbol '" + variableName + "'", caller));
			return;
		}
		
		switch(src.type) {
		case BOOLEAN:
			ScriptExecutor.raiseError(new ScriptError("Cannot add a value to a boolean.", caller));
			break;
		case DECIMAL:
			double d = this.readVariableDouble(src);
			if(value.type == Type.DECIMAL)
				d += value.d;
			else if(value.type == Type.INT)
				d += value.i;
			else if(value.type == Type.BOOLEAN)
				d += value.b ? 1 : 0;
			else if(value.type == Type.STRING) {
				ScriptExecutor.raiseError(new ScriptError("Cannot add text to a decimal.", caller));
				return;
			} else if(value.type == Type.OBJREF) {
				ScriptExecutor.raiseError(new ScriptError("Cannot perform operations with objects (Users, Channels, etc...)", caller));
				return;
			}
			this.writeVariable(src.name, d);
			break;
		case INT:
			long i = this.readVariableInt(src);
			if(value.type == Type.DECIMAL)
				i += (int)value.d;
			else if(value.type == Type.INT)
				i += value.i;
			else if(value.type == Type.BOOLEAN)
				i += value.b ? 1 : 0;
			else if(value.type == Type.STRING) {
				ScriptExecutor.raiseError(new ScriptError("Cannot add text to an integer.", caller));
				return;
			} else if(value.type == Type.OBJREF) {
				ScriptExecutor.raiseError(new ScriptError("Cannot perform operations with objects (Users, Channels, etc...)", caller));
				return;
			}
			this.writeVariable(src.name, i);
			break;
		case STRING:
			String str = this.readVariableString(src);
			str += value.getConcatString();
			this.writeVariable(src.name, str);
			break;
		case OBJREF:
			ScriptExecutor.raiseError(new ScriptError("Cannot perform operations with objects (Users, Channels, etc...)", caller));
			return;
		default:
			break;
		}
	}
	public void operationSubConstant(String variableName, DynamicValue value, ScriptStatement caller) {
		
		Symbol src = this.getSymbol(variableName);
		
		if(src == null) {
			ScriptExecutor.raiseError(new ScriptError("Undefined symbol '" + variableName + "'", caller));
			return;
		}
		
		switch(src.type) {
		case BOOLEAN:
			ScriptExecutor.raiseError(new ScriptError("Cannot subtract from a boolean.", caller));
			break;
		case DECIMAL:
			double d = this.readVariableDouble(src);
			if(value.type == Type.DECIMAL)
				d -= value.d;
			else if(value.type == Type.INT)
				d -= value.i;
			else if(value.type == Type.BOOLEAN)
				d -= value.b ? 1 : 0;
			else if(value.type == Type.STRING) {
				ScriptExecutor.raiseError(new ScriptError("Cannot subtract text from a decimal.", caller));
				return;
			} else if(value.type == Type.OBJREF) {
				ScriptExecutor.raiseError(new ScriptError("Cannot perform operations with objects (Users, Channels, etc...)", caller));
				return;
			}
			this.writeVariable(src.name, d);
			break;
		case INT:
			long i = this.readVariableInt(src);
			if(value.type == Type.DECIMAL)
				i -= (int)value.d;
			else if(value.type == Type.INT)
				i -= value.i;
			else if(value.type == Type.BOOLEAN)
				i -= value.b ? 1 : 0;
			else if(value.type == Type.STRING) {
				ScriptExecutor.raiseError(new ScriptError("Cannot subtract text from an integer.", caller));
				return;
			} else if(value.type == Type.OBJREF) {
				ScriptExecutor.raiseError(new ScriptError("Cannot perform operations with objects (Users, Channels, etc...)", caller));
				return;
			}
			this.writeVariable(src.name, i);
			break;
		case STRING:
			String str = this.readVariableString(src);
			String dst = value.getConcatString();
			if(str.endsWith(dst)) {
				str = str.substring(0, str.length() - dst.length());
				this.writeVariable(src.name, str);
			} else {
				ScriptExecutor.raiseError(new ScriptError("Cannot subtract text from another text unless A ends with B.", caller));
			}
			break;
		case OBJREF:
			ScriptExecutor.raiseError(new ScriptError("Cannot perform operations with objects (Users, Channels, etc...)", caller));
			return;
		default:
			break;
		}
	}
	public void operationMulConstant(String variableName, DynamicValue value, ScriptStatement caller) {
		
		Symbol src = this.getSymbol(variableName);
		
		if(src == null) {
			ScriptExecutor.raiseError(new ScriptError("Undefined symbol '" + variableName + "'", caller));
			return;
		}
		
		switch(src.type) {
		case BOOLEAN:
			ScriptExecutor.raiseError(new ScriptError("Cannot multiply a boolean.", caller));
			break;
		case DECIMAL:
			double d = this.readVariableDouble(src);
			if(value.type == Type.DECIMAL)
				d *= value.d;
			else if(value.type == Type.INT)
				d *= value.i;
			else if(value.type == Type.BOOLEAN)
				d *= value.b ? 1 : 0;
			else if(value.type == Type.STRING) {
				ScriptExecutor.raiseError(new ScriptError("Cannot multiply a decimal with text.", caller));
				return;
			} else if(value.type == Type.OBJREF) {
				ScriptExecutor.raiseError(new ScriptError("Cannot perform operations with objects (Users, Channels, etc...)", caller));
				return;
			}
			this.writeVariable(src.name, d);
			break;
		case INT:
			long i = this.readVariableInt(src);
			if(value.type == Type.DECIMAL)
				i *= (int)value.d;
			else if(value.type == Type.INT)
				i *= value.i;
			else if(value.type == Type.BOOLEAN)
				i *= value.b ? 1 : 0;
			else if(value.type == Type.STRING) {
				ScriptExecutor.raiseError(new ScriptError("Cannot multiply an integer with text.", caller));
				return;
			} else if(value.type == Type.OBJREF) {
				ScriptExecutor.raiseError(new ScriptError("Cannot perform operations with objects (Users, Channels, etc...)", caller));
				return;
			}
			this.writeVariable(src.name, i);
			break;
		case STRING:
			String str = this.readVariableString(src);
			double factor = str.length();
			if(value.type == Type.DECIMAL)
				factor = value.d;
			else if(value.type == Type.INT)
				factor = (double)value.i;
			else if(value.type == Type.BOOLEAN)
				factor = value.b ? 1.0 : 2.0;
			else if(value.type == Type.STRING) {
				ScriptExecutor.raiseError(new ScriptError("Cannot multiply text with text.", caller));
				return;
			} else if(value.type == Type.OBJREF) {
				ScriptExecutor.raiseError(new ScriptError("Cannot perform operations with objects (Users, Channels, etc...)", caller));
				return;
			}
			char[] chars = str.toCharArray();
			int newLen = (int)(str.length() * factor);
			StringBuilder rebuild = new StringBuilder();
			int pull = 0;
			for(int x = 0; x < newLen; x++) {
				rebuild.append(chars[pull++]);
				if(pull >= chars.length)
					pull = 0;
			}
			this.writeVariable(src.name, rebuild.toString());
			break;
		case OBJREF:
			ScriptExecutor.raiseError(new ScriptError("Cannot perform operations with objects (Users, Channels, etc...)", caller));
			return;
		default:
			break;
		}
	}
	public void operationDivConstant(String variableName, DynamicValue value, ScriptStatement caller) {
		
		Symbol src = this.getSymbol(variableName);
		
		if(src == null) {
			ScriptExecutor.raiseError(new ScriptError("Undefined symbol '" + variableName + "'", caller));
			return;
		}
		
		switch(src.type) {
		case BOOLEAN:
			ScriptExecutor.raiseError(new ScriptError("Cannot divide a boolean.", caller));
			break;
		case DECIMAL:
			double d = this.readVariableDouble(src);
			if(value.type == Type.DECIMAL)
				d /= value.d;
			else if(value.type == Type.INT)
				d /= value.i;
			else if(value.type == Type.BOOLEAN && value.b) {
				ScriptExecutor.raiseError(new ScriptError("Cannot divide a decimal with a boolean.", caller));
				return;
			} else if(value.type == Type.STRING) {
				ScriptExecutor.raiseError(new ScriptError("Cannot divide a decimal with text.", caller));
				return;
			} else if(value.type == Type.OBJREF) {
				ScriptExecutor.raiseError(new ScriptError("Cannot perform operations with objects (Users, Channels, etc...)", caller));
				return;
			}
			this.writeVariable(src.name, d);
			break;
		case INT:
			long i = this.readVariableInt(src);
			if(value.type == Type.DECIMAL)
				i /= (int)value.d;
			else if(value.type == Type.INT)
				i /= value.i;
			else if(value.type == Type.BOOLEAN) {
				ScriptExecutor.raiseError(new ScriptError("Cannot divide an integer with a boolean.", caller));
				return;
			} else if(value.type == Type.STRING) {
				ScriptExecutor.raiseError(new ScriptError("Cannot divide an integer with text.", caller));
				return;
			} else if(value.type == Type.OBJREF) {
				ScriptExecutor.raiseError(new ScriptError("Cannot perform operations with objects (Users, Channels, etc...)", caller));
				return;
			}
			this.writeVariable(src.name, i);
			break;
		case STRING:
			ScriptExecutor.raiseError(new ScriptError("Cannot divide text.", caller));
			break;
		case OBJREF:
			ScriptExecutor.raiseError(new ScriptError("Cannot perform operations with objects (Users, Channels, etc...)", caller));
			return;
		default:
			break;
		}
	}
	public void operationModConstant(String variableName, DynamicValue value, ScriptStatement caller) {
		
		Symbol src = this.getSymbol(variableName);
		
		if(src == null) {
			ScriptExecutor.raiseError(new ScriptError("Undefined symbol '" + variableName + "'", caller));
			return;
		}
		
		switch(src.type) {
		case BOOLEAN:
			ScriptExecutor.raiseError(new ScriptError("Cannot modulo a boolean.", caller));
			break;
		case DECIMAL:
			double d = this.readVariableDouble(src);
			if(value.type == Type.DECIMAL)
				d /= value.d;
			else if(value.type == Type.INT)
				d /= value.i;
			else if(value.type == Type.BOOLEAN && value.b) {
				ScriptExecutor.raiseError(new ScriptError("Cannot divide a decimal with a boolean.", caller));
				return;
			} else if(value.type == Type.OBJREF) {
				ScriptExecutor.raiseError(new ScriptError("Cannot perform operations with objects (Users, Channels, etc...)", caller));
				return;
			}
			this.writeVariable(src.name, d);
			break;
		case INT:
			long i = this.readVariableInt(src);
			if(value.type == Type.DECIMAL)
				i /= (int)value.d;
			else if(value.type == Type.INT)
				i /= value.i;
			else if(value.type == Type.BOOLEAN) {
				ScriptExecutor.raiseError(new ScriptError("Cannot divide an integer with a boolean.", caller));
				return;
			} else if(value.type == Type.OBJREF) {
				ScriptExecutor.raiseError(new ScriptError("Cannot perform operations with objects (Users, Channels, etc...)", caller));
				return;
			}
			this.writeVariable(src.name, i);
			break;
		case STRING:
			ScriptExecutor.raiseError(new ScriptError("Cannot divide text.", caller));
			break;
		case OBJREF:
			ScriptExecutor.raiseError(new ScriptError("Cannot perform operations with objects (Users, Channels, etc...)", caller));
			return;
		default:
			break;
		}
	}
}