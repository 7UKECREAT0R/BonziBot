package com.lukecreator.BonziBot.Graphics;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.RandomStringUtils;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.io.RawInputStreamFacade;

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
	/**
	 * This implementation shies away from Font.createFont(type, stream) since it crashes
	 * in any environment where the Java temp folder is inaccessible and even when it is,
	 * the loading just doesn't go through all the way. This implementation guarantees that
	 * the font will be completely unpacked and readable for the font loader.
	 * @param rescPath
	 */
	public static void registerFont(String rescPath) {
		try {
			String tempDirectory = System.getProperty("java.io.tmpdir");
			File fontFile = new File(tempDirectory + "/font" + RandomStringUtils.randomAlphanumeric(12) + ".ttf");
			fontFile.deleteOnExit();
			
			// move font file out of resources into java.io.tmpdir
			InputStream stream = getResource(rescPath);
			FileUtils.copyStreamToFile(new RawInputStreamFacade(stream), fontFile);
			
			Font newFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
			GraphicsEnvironment current = GraphicsEnvironment.getLocalGraphicsEnvironment();
			current.registerFont(newFont);
			InternalLogger.print("Loaded font \"" + newFont.getFontName() + "\".");
			stream.close();
		} catch(IOException|FontFormatException exc) {
			exc.printStackTrace();
		}
	}
}
