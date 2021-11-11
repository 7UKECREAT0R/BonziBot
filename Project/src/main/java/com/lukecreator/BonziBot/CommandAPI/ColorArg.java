package com.lukecreator.BonziBot.CommandAPI;

import java.awt.Color;

import com.lukecreator.BonziBot.BonziUtils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

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
			"yellow",
			"purple"
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
			new Color(143, 0, 209)
	};
	
	public ColorArg(String name) {
		super(name);
		this.type = ArgType.Color;
	}
	
	@Override
	public boolean isWordParsable(String word, Guild theGuild) {
		
		// Enum value case.
		word = word.replace("_", "").replace(" ", "");
		for(int i = 0; i < colors.length; i++) {
			String cWord = colorWords[i];
			if(word.equalsIgnoreCase(cWord)) {
				return true;
			}
		}
		
		// Hex color case.
		int len = word.length();
		if(word.startsWith("#") && len == 7)
			return true;
		
		// RGB color case.
		boolean anyParen = hasParenthesis(word);
		int commaCount = BonziUtils.countChars(word, ',');
		boolean validCommas = (commaCount == 2);
		if(anyParen & validCommas) {
			String strip = stripParenthesis(word);
			String[] parts = strip.split(",");
			if(parts.length < 3)
				return false;
			try {
				Integer.parseInt(parts[0].trim());
				Integer.parseInt(parts[1].trim());
				Integer.parseInt(parts[2].trim());
				return true;
			} catch(NumberFormatException nfe) {
				return false;
			}
		}
		
		return false;
	}
	
	@Override
	public void parseWord(String word, JDA jda, User user, Guild theGuild) {
		word = word.replace("_", "").replace(" ", "");
		for(int i = 0; i < colors.length; i++) {
			String cWord = colorWords[i];
			if(word.equalsIgnoreCase(cWord)) {
				this.object = colors[i];
				return;
			}
		}
		
		if(word.startsWith("#")) {
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
			} catch(NumberFormatException nfe) {
				nfe.printStackTrace();
				this.object = null;
			}
			return;
		}
		
		if(hasParenthesis(word)) {
			String strip = stripParenthesis(word);
			String[] parts = strip.split(",");
			if(parts.length < 3) {
				this.object = null;
				return;
			}
			String _p1 = parts[0].trim();
			String _p2 = parts[1].trim();
			String _p3 = parts[2].trim();
			int p1 = Integer.parseInt(_p1);
			int p2 = Integer.parseInt(_p2);
			int p3 = Integer.parseInt(_p3);
			p1 = BonziUtils.clamp(p1, 0, 255);
			p2 = BonziUtils.clamp(p2, 0, 255);
			p3 = BonziUtils.clamp(p3, 0, 255);
			if(p1 == 255 && p1 == p2 && p2 == p3) {
				p1 = 254;
				p2 = 254;
				p3 = 254;
			}
			Color col = new Color(p1, p2, p3);
			this.object = col;
			return;
		}
		
		this.object = null;
		return;
	}
	
	public boolean hasParenthesis(String s) {
		boolean paren1 = s.startsWith("[") && s.endsWith("]");
		boolean paren2 = s.startsWith("(") && s.endsWith(")");
		boolean paren3 = s.startsWith("{") && s.endsWith("}");
		return paren1 || paren2 || paren3;
	}
	public String stripParenthesis(String s) {
		int length = s.length();
		return s.substring(1, length - 1);
	}
	
	@Override
	public String getErrorDescription() {
		return "You need to specify a valid color. (red, green, #1EFF1E, [255,0,0], etc...)";
	}
	
	@Override
	public String stringify(Object obj) {
		if(obj == null)
			return null;
		if(obj instanceof Color) {
			Color c = (Color)obj;
			return "[" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + "]";
		} else
			return obj.toString();
	}
}
