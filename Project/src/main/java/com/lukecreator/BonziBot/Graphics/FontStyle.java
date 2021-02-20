package com.lukecreator.BonziBot.Graphics;

public enum FontStyle {
	NORMAL(0),
	BOLD(1),
	ITALIC(2);
	
	public final int constant;
	FontStyle(int constant) {
		this.constant = constant;
	}
}
