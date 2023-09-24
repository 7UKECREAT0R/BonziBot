package com.lukecreator.BonziBot.Script.Editor;

import java.lang.reflect.InvocationTargetException;

import com.lukecreator.BonziBot.InternalLogger;
import com.lukecreator.BonziBot.Script.Model.ScriptGetter;
import com.lukecreator.BonziBot.Script.Model.ScriptStatement;

/**
 * Wraps a statement-implementing class and provides user information about it.
 * This class is pretty messy but it should work well enough.
 * @author Lukec
 */
public class StatementDescriptor {
	
	public final boolean isGetter;
	public final Class<? extends ScriptGetter> getter;
	public final Class<? extends ScriptStatement> internalClass;
	public final String name;
	public final String desc;
	
	private String keyword = null;
	public String getKeyword() {
		return this.keyword;
	}
	
	@SuppressWarnings("unchecked") // it's fine since i'm hardcoding these
	public StatementDescriptor(Class<? extends ScriptStatement> clazz, String name, String desc) {
		this.isGetter = clazz.isAssignableFrom(ScriptGetter.class);
		if(this.isGetter) {
			this.getter = (Class<? extends ScriptGetter>)clazz;
			this.internalClass = null;
		} else {
			this.getter = null;
			this.internalClass = clazz;
		}
		this.name = name;
		this.desc = desc;
		
		try {
			this.keyword = clazz.getDeclaredConstructor().newInstance().getKeyword();
		} catch (InstantiationException | IllegalAccessException e) {
			InternalLogger.printError(e);
		} catch (IllegalArgumentException e) {
			InternalLogger.printError(e);
		} catch (InvocationTargetException e) {
			InternalLogger.printError(e);
		} catch (NoSuchMethodException e) {
			InternalLogger.printError(e);
		} catch (SecurityException e) {
			InternalLogger.printError(e);
		}
	}
	@SuppressWarnings("unchecked")
	public StatementDescriptor(Class<? extends ScriptStatement> clazz, boolean isGetter, String name, String desc) {
		this.isGetter = isGetter;
		if(this.isGetter) {
			this.getter = (Class<? extends ScriptGetter>)clazz;
			this.internalClass = null;
		} else {
			this.getter = null;
			this.internalClass = clazz;
		}
		
		this.name = name;
		this.desc = desc;
		try {
			this.keyword = clazz.getDeclaredConstructor().newInstance().getKeyword();
		} catch (InstantiationException | IllegalAccessException e) {
			InternalLogger.printError(e);
		} catch (IllegalArgumentException e) {
			InternalLogger.printError(e);
		} catch (InvocationTargetException e) {
			InternalLogger.printError(e);
		} catch (NoSuchMethodException e) {
			InternalLogger.printError(e);
		} catch (SecurityException e) {
			InternalLogger.printError(e);
		}
	}
	public ScriptStatement createNewFromClass() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		if(this.isGetter)
			return null;
		return this.internalClass.getDeclaredConstructor().newInstance();
	}
	public ScriptStatement createNewFromGetter(String[] inputs) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		if(!this.isGetter)
			return null;
		ScriptGetter getter = this.getter.getDeclaredConstructor().newInstance();
	
		if(getter.getsObjectFromContext()) {
			getter.field = inputs[0];
			getter.destination = inputs[1];
		} else {
			getter.object = inputs[0];
			getter.field = inputs[1];
			getter.destination = inputs[2];
		}
		
		return getter;
	}
}
