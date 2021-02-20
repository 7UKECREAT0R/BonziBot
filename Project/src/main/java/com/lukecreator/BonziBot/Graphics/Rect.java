package com.lukecreator.BonziBot.Graphics;

/**
 *     A good translator for different forms of
 * rectangles, aka width/height and corner rectangles.
 */
public class Rect {
	
	public final int left, right, top, bottom;
	public final int x, y, width, height;
	
	private Rect(int left, int right, int top, int bottom, int x, int y, int width, int height) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	// Factories
	public static Rect fromCorners(int x1, int y1, int x2, int y2) {
		int left = Math.min(x1, x2);
		int right = Math.max(x1, x2);
		int top = Math.min(y1, y2);
		int bottom = Math.max(y1, y2);
		return new Rect(left, right, top, bottom,
			left, top, right - left, bottom - top);
	}
	public static Rect fromSides(int left, int right, int top, int bottom) {
		return new Rect(left, right, top, bottom,
			left, top, right - left, bottom - top);
	}
	public static Rect fromDimensions(int x, int y, int width, int height) {
		return new Rect(x, x + width, y, y + height, x, y, width, height);
	}
	
	/**
	 * Returns if these rectangles intersect each other.
	 */
	public boolean intersects(Rect other) {
		return (this.left < other.right && this.right > other.left &&
			this.top < other.bottom && this.bottom > other.top);
	}
	/**
	 * Returns a new rect that is the joining of two rectangles.
	 */
	public Rect join(Rect other) {
		int left = Math.min(this.left, other.left);
		int right = Math.max(this.right, other.right);
		int top = Math.min(this.top, other.top);
		int bottom = Math.max(this.bottom, other.bottom);
		return new Rect(left, right, top, bottom,
			left, top, right - left, bottom - top);
	}
	
	public int getArea() {
		return width * height;
	}
	public int getCenterX() {
		int half = width / 2;
		return x + half;
	}
	public int getCenterY() {
		int half = height / 2;
		return y + half;
	}
}