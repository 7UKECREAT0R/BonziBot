package com.lukecreator.BonziBot.CommandAPI;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import com.lukecreator.BonziBot.BonziUtils;

import net.dv8tion.jda.api.JDA;

/*
 * Danger zone lmao (I have no idea what I'm doing here)
 * Masks out any enum values which begin with an underscore.
 */
@SuppressWarnings("rawtypes")
public class EnumArg extends CommandArg {
	
	Class<? extends Enum> baseClass;
	Enum[] enumType;
	
	@SuppressWarnings("unchecked")
	public EnumArg(String name, Class inClass) {
		super(name);
		this.type = ArgType.Enum;
		
		if(!inClass.isEnum())
			throw new InvalidParameterException("Passed Enum class was not defined as a valid Enum.");
		
		baseClass = (Class<? extends Enum>)inClass;
		enumType = (Enum[])inClass.getEnumConstants();
	}
	
	@Override
	public boolean isWordParsable(String word) {
		word = word.toUpperCase();
		for(Enum e: enumType) {
			if(e.name().toUpperCase().contains(word)
			&& !e.name().startsWith("_"))
				return true;
		}
		return false;
	}
	
	@Override
	public void parseWord(String word, JDA jda) {
		word = word.toUpperCase();
		for(Enum e: enumType) {
			if(e.name().toUpperCase().contains(word)
			&& !e.name().startsWith("_")) {
				this.object = e;
				return;
			}
		}
	}
	
	@Override
	public String getErrorDescription() {
		List<String> names = new ArrayList<String>();
		for(int i = 0; i < enumType.length; i++) {
			if(enumType[i].name().startsWith("_"))
				continue;
			names.add(enumType[i].name().toLowerCase());
		}
		return "This can be any of the following values (or shortened):\n" + BonziUtils.stringJoinOr(", ", names);
	}
}