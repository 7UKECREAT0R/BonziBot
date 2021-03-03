package com.lukecreator.BonziBot.Graphics;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

import com.lukecreator.BonziBot.InternalLogger;

/**
 * Loads resource based fonts.
 */
public class FontLoader {
	
	public static final String THE_BOLD_FONT_FILE = "fonts/theboldfont.ttf";
	public static final String THE_BOLD_FONT = "The Bold Font";
	public static final String BEBAS_FONT_FILE = "fonts/bebas.ttf";
	public static final String BEBAS_FONT = "Bebas";
	public static final String EMOJI_FONT_FILE = "fonts/emoji.ttf";
	public static final String EMOJI_FONT = "OpenSansEmoji";
	public static final String SEGOE_FONT_FILE = "fonts/segoeui.ttf";
	public static final String SEGOE_FONT = "Segoe UI";
	
	private static InputStream getResource(String path) {
		ClassLoader loader = FontLoader.class.getClassLoader();
		return loader.getResourceAsStream(path);
	}
	public static void registerFont(String rescPath) {
		try {
			InputStream stream = getResource(rescPath);
			Font newFont = Font.createFont(Font.TRUETYPE_FONT, stream);
			GraphicsEnvironment current = GraphicsEnvironment.getLocalGraphicsEnvironment();
			current.registerFont(newFont);
			InternalLogger.print("Loaded font \"" + newFont.getFontName() + "\".");
			stream.close();
		} catch(IOException|FontFormatException exc) {
			exc.printStackTrace();
		}
	}
}
