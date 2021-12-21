package com.lukecreator.BonziBot.Profiles;

import java.io.Serializable;

import com.lukecreator.BonziBot.Graphics.Image;

// NOTE: profile modules are still in progress... will work on them when i get the chance

public interface ProfileModule extends Serializable {
	
	/**
	 * Draw this module onto the profile image.
	 * @param image The image to draw to.
	 * @param x The x-position of the location to draw this module.
	 * @param y The y-position of the location to draw this module.
	 * @param width The width this element should be to conform with padding.
	 */
	public void draw(Image image, int x, int y, int width);
	/**
	 * Get the height of this module. Used in pre-calculations.
	 * @return
	 */
	public int getHeight();
	
	
}
