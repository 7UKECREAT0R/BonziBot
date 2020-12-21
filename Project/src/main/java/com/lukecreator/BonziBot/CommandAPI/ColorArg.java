package com.lukecreator.BonziBot.CommandAPI;

import java.awt.Color;

import net.dv8tion.jda.api.JDA;

public class ColorArg extends CommandArg {
	
	public static final String[] colorWords = new String[] {
			"black",
			"cyan",
			"blue",
			"darkgray",
			"gray",
			"green",
			"lightgray",
			"magenta",
			"orange",
			"pink",
			"red",
			"white",
			"yellow"
	};
	public static final Color[] colors = new Color[] {
			Color.BLACK,
			Color.CYAN,
			Color.BLUE,
			Color.DARK_GRAY,
			Color.GRAY,
			Color.GREEN,
			Color.LIGHT_GRAY,
			Color.MAGENTA,
			Color.ORANGE,
			Color.PINK,
			Color.RED,
			new Color(254, 254, 254),
			Color.YELLOW,
	};
	
	public ColorArg(String name) {
		super(name);
		this.type = ArgType.Color;
	}
	
	@Override
	public boolean isWordParsable(String word) {
		word = word.replace("_", "").replace(" ", "");
		for(int i = 0; i < colors.length; i++) {
			String cWord = colorWords[i];
			if(word.equalsIgnoreCase(cWord)) {
				return true;
			}
		}
		
		int len = word.length();
		return (word.startsWith("#") && len == 7);
	}
	
	@Override
	public void parseWord(String word, JDA jda) {
		word = word.replace("_", "").replace(" ", "");
		for(int i = 0; i < colors.length; i++) {
			String cWord = colorWords[i];
			if(word.equalsIgnoreCase(cWord)) {
				this.object = colors[i];
				return;
			}
		}
		
		if(!word.startsWith("#")) {
			this.object = null;
			return;
		}
		int len = word.length();
		if(len != 7) {
			this.object = null;
			return;
		}
		
		try {
			Color col = Color.decode(word.toUpperCase());
			if(col.getRed() == 255 && col.getGreen() == 255 && col.getBlue() == 255)
				col = new Color(254, 254, 254); // Pure white doesn't display right on embeds.
			this.object = col;
			return;
		} catch(NumberFormatException nfe) {
			nfe.printStackTrace();
			this.object = null;
			return;
		}
	}
	
	@Override
	public String getErrorDescription() {
		return "You need to specify a valid color. (red, green, #1EFF1E, etc...)";
	}
}
