package com.lukecreator.BonziBot.Data;

import java.util.Comparator;

public class TagSort implements Comparator<TagData> {
	@Override
	public int compare(TagData o1, TagData o2) {
		return o1.uses - o2.uses;
	}
}
