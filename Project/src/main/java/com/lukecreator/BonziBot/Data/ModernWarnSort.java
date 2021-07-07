package com.lukecreator.BonziBot.Data;

import java.util.Comparator;

public class ModernWarnSort implements Comparator<ModernWarn> {

	@Override
	public int compare(ModernWarn o1, ModernWarn o2) {
		return (int)(o1.timestamp - o2.timestamp);
	}
	
}
