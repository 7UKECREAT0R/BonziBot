package com.lukecreator.BonziBot.CommandAPI;

public enum ArgsComparison {
	
	/*
	 * args.length == goal
	 */
	EQUAL,
	
	/*
	 * args.length >= goal
	 */
	ANY_HIGHER,
	
	/*
	 * args.length <= goal
	 */
	ANY_LOWER,
	
}
