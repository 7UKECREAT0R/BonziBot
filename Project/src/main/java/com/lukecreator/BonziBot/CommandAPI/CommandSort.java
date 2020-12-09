package com.lukecreator.BonziBot.CommandAPI;

import java.util.Comparator;

public class CommandSort implements Comparator<Command> {
	public int compare(Command a, Command b) {
		return a.subCategory - b.subCategory;
	}
}