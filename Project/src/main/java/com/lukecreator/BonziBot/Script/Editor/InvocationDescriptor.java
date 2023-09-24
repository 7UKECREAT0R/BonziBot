package com.lukecreator.BonziBot.Script.Editor;

import java.lang.reflect.InvocationTargetException;

import com.lukecreator.BonziBot.Script.Model.InvocationMethod;

/**
 * Wraps an invocationmethod-implementing class and provides user information about it.
 * @author Lukec
 */
public class InvocationDescriptor {
	
	public final Class<? extends InvocationMethod> internalClass;
	public final String name;
	public final String desc;
	
	public InvocationDescriptor(Class<? extends InvocationMethod> internalClass, String name, String desc) {
		this.internalClass = internalClass;
		this.name = name;
		this.desc = desc;
	}
	public InvocationMethod createNew() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		return this.internalClass.getDeclaredConstructor().newInstance();
	}
}
