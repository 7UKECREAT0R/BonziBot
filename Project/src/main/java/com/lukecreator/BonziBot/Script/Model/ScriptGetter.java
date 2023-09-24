package com.lukecreator.BonziBot.Script.Model;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.StringArg;
import com.lukecreator.BonziBot.GuiAPI.DropdownItem;
import com.lukecreator.BonziBot.GuiAPI.GuiDropdown;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntryChoice;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntryText;
import com.lukecreator.BonziBot.Script.Editor.StatementCategory;

import net.dv8tion.jda.api.entities.Guild;

/**
 * Provides an efficient means of mass producing getters for objects.
 * @author Lukec
 */
public abstract class ScriptGetter implements ScriptStatement {
	
	private static final long serialVersionUID = 1L;

	public transient BonziBot bonziInstance;
	
	// Hacky serializable function!
	public interface SerializableFunction extends Function<Object, Object>, Serializable {}
	
	/**
	 * Names a transformer function which aims to wrap a getter method
	 * On a script-interfaced object. Acts like user-facing reflection.<br><br>
	 * 
	 * The type of the object which will be passed in will be determined
	 * by the {@link ScriptGetter#requiredType} this is placed in.<br><br>
	 * 
	 * If {@link ScriptGetter#requiredType} is null, then a {@link ScriptContextInfo}
	 * will be passed in and the user will not be prompted to specify an object.
	 * This indicates that the argument should be contextually identified.
	 * <br><br>
	 * 
	 * <code>
	 * <br>new Binding("Server Name", g -> {
	 * <br>	return ((Guild)g).getName();
	 * <br>});
	 * </code>
	 * @author Lukec
	 */
	public class Binding implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		public final String name;
		public final SerializableFunction transformer;
		
		public Binding(String name, SerializableFunction transformer) {
			this.name = name;
			this.transformer = transformer;
		}
	}
	
	public String object;					// Source variable which holds the object reference
	public String field;					// The name of the binding to try to get from.
	public String destination;				// The destination to which the gotten object will then hold.
	
	// All five of these need to be superclass-assigned. \/
	
	protected String nameOfType;					// The name of the source object's type required.
	protected String keyword;						// The internal keyword of the statement.
	protected Class<? extends Object> requiredType;	// The type needed for the source to be/extend.
	protected List<Binding> propertyBindings;		// The properties that can be transformed.
	
	/**
	 * Whether this can get its object from context, not needing it to be passed in.
	 * @return
	 */
	public boolean getsObjectFromContext() {
		if(this.requiredType == null)
			return false;
		return this.requiredType.equals(ScriptContextInfo.class);
	}
	
	@Override
	public String getKeyword() {
		return this.keyword;
	}

	@Override
	public String getAsCode() {
		if(this.getsObjectFromContext()) {
			String f = Script.asArgument(this.field);
			String b = Script.asArgument(this.destination);
			return this.keyword + ' ' + f + ' ' + b;
		} else {
			String f = Script.asArgument(this.field);
			String a = Script.asArgument(this.object);
			String b = Script.asArgument(this.destination);
			return this.keyword + ' ' + a + ' ' + f + ' ' + b;
		}
	}

	@Override
	public GuiEditEntry[] getArgs(Script caller, Guild server) {
		int i = 0;
		String[] _choices = new String[this.propertyBindings.size()];
		for(Binding binding: this.propertyBindings)
			_choices[i++] = '`' + binding.name + '`';
		String choices = BonziUtils.stringJoinOr(", ", _choices);
		
		GuiDropdown fieldSelector = new GuiDropdown("Field to get...", "field", false);
		fieldSelector.addItemsTransform(this.propertyBindings, binding -> {
			return new DropdownItem(binding.name, binding.name);
		});
		
		if(this.getsObjectFromContext()) {
			return new GuiEditEntry[] {
					new GuiEditEntryChoice(fieldSelector, "🗂️", "Field", "Available options:\n" + choices),
				new GuiEditEntryText(new StringArg("dst"), "📩", "Destination Variable", "The variable that the data will be placed in.")
			};
		} else {
			return new GuiEditEntry[] {
				caller.createVariableChoice("🏀", BonziUtils.titleString(this.nameOfType), "The " + this.nameOfType.toLowerCase() + " to get the data from."),
				new GuiEditEntryChoice(fieldSelector, "🗂️", "Field", "Available options:\n" + choices),
				new GuiEditEntryText(new StringArg("dst"), "📩", "Destination Variable", "The variable that the data will be placed in.")
			};
		}
	}
	
	@Override
	public String getNewVariable() {
		return this.destination;
	}

	@Override
	public StatementCategory getCategory() {
		return StatementCategory.DATA;
	}

	@Override
	public void parse(Object[] inputs) {
		if(this.getsObjectFromContext()) {
			this.field = (String)inputs[0];
			this.destination = (String)inputs[1];
		} else {
			this.object = (String)inputs[0];
			this.field = (String)inputs[1];
			this.destination = (String)inputs[2];
		}
	}

	@Override
	public void execute(ScriptContextInfo info, ScriptExecutor context) {
		
		if(!info.hasGuild) {
			ScriptExecutor.raiseError(new ScriptError("Apparently this wasn't run in a valid server... uhhh, try again...", this));
			return;
		}
		
		Object obj;
		
		if(this.getsObjectFromContext()) {
			obj = info;
		} else {
			obj = context.memory.readObjectRef(this.object);
			if(obj == null) {
				ScriptExecutor.raiseError(new ScriptError("Variable '" + this.object + "' doesn't exist or is not an object.", this));
				return;
			}
		}
		
		Class<? extends Object> clazz = obj.getClass();
		if(clazz.isAssignableFrom(this.requiredType)) {
			ScriptExecutor.raiseError(new ScriptError("Variable '" + this.object + "' is not a " + this.nameOfType + ". It's a " + clazz.getSimpleName(), this));
			return;
		}
		
		for(Binding bind: this.propertyBindings) {
			if(bind.name.equalsIgnoreCase(this.field)) {
				// Get the defined transformer.
				Function<Object, Object> func = bind.transformer;
				// Apply it to the object.
				Object output = func.apply(obj);
				// Write to memory.
				context.memory.writeVariableUnknownType(this.destination, output);
				return;
			}
		}
		
		ScriptExecutor.raiseError(new ScriptError("No field named '" + this.field + "'.", this));
		return;
	}

}
