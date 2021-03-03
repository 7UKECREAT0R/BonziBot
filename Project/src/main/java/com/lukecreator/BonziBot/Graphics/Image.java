package com.lukecreator.BonziBot.Graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Map;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.DataSerializer;

/**
 * wrapper for all this graphicz stuf
 */
public class Image {
	
	static final double MAX_FILE_SIZE = 8.0; // MB
	
	int width, height;
	int imageFormat;
	boolean hasFont;
	final boolean isPng;
	float imageQuality = 0.95f;
	Rect bounds;
	Color color, bgColor;
	BufferedImage image;
	Graphics2D graphics;
	FontMetrics fontMetrics;
	Font font;
	
	public Image(int width, int height, boolean transparent) {
		this.hasFont = false;
		this.width = width;
		this.height = height;
		this.isPng = transparent;
		this.image = new BufferedImage(width, height,
			transparent ? BufferedImage.TYPE_INT_ARGB
				: BufferedImage.TYPE_INT_RGB);
		this.graphics = image.createGraphics();
		this.bounds = Rect.fromDimensions(0, 0, width, height);
		this.setupRenderingHints();
	}
	public Image(BufferedImage image) {
		this.hasFont = false;
		this.width = image.getWidth();
		this.height = image.getHeight();
		this.isPng = image.getType() == BufferedImage.TYPE_INT_ARGB;
		this.image = image;
		this.graphics = image.createGraphics();
		this.bounds = Rect.fromDimensions(0, 0, width, height);
		this.setupRenderingHints();
	}
	public static String downloadMessage = "";
	public static Image download(String url) {
		try {
			URL urlObject = new URL(url);
			double size = BonziUtils.getFileSizeMb(urlObject);
			System.out.println("size (mb): " + size);
			if(size > MAX_FILE_SIZE) {
				downloadMessage = "Your file can't be over " + MAX_FILE_SIZE + "mb.";
				return null;
			}
			HttpURLConnection connection = (HttpURLConnection)urlObject.openConnection();
			connection.setRequestProperty("User-Agent", BonziUtils.USER_AGENT);
			BufferedImage image = ImageIO.read(connection.getInputStream());
			connection.disconnect();
			if(image == null) {
				downloadMessage = "Downloaded image doesn't exist anymore.";
				return null;
			}
			downloadMessage = "Successful.";
			return new Image(image);
		} catch (MalformedURLException e) {
			downloadMessage = "Malformed URL. Try setting the image again?";
			return null;
		} catch (IOException e) {
			downloadMessage = e.getMessage();
			return null;
		}
	}
	
	public void setupRenderingHints() {
        this.graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        this.graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        this.graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        this.graphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        this.graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        this.graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        this.graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        this.graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
	}
	public void dispose() {
		if(graphics != null)
			graphics.dispose();
		if(image != null)
			image.flush();
	}
	public int getWidth() {
		return this.width;
	}
	public int getHeight() {
		return this.height;
	}
	public FontMetrics getFontMetrics() {
		return this.fontMetrics;
	}
	public void setCompressionQuality(float quality) {
		if(quality < 0.0f) quality = 0.0f;
		if(quality > 1.0f) quality = 1.0f;
		this.imageQuality = quality;
	}
	public Rect getBounds() {
		return this.bounds;
	}
	public String getFileType() {
		return this.isPng ? "png" : "jpg";
	}
	public File save(String path, boolean dispose) throws IOException {
		File output = new File(DataSerializer.baseDataPath + path);
		
		if(this.isPng) {
			ImageIO.write(this.image, "png", output);
			if(dispose) this.dispose();
			return output;
		}
		
		JPEGImageWriteParam settings = new JPEGImageWriteParam(null);
		settings.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		settings.setCompressionQuality(imageQuality);
		
		FileImageOutputStream stream = new FileImageOutputStream(output);
		ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
		writer.setOutput(stream);
		writer.write(null, new IIOImage(this.image, null, null), settings);
		
		writer.dispose();
		stream.close();
		if(dispose)
			this.dispose();
		return output;
	}
	
	public Image clear() {
		return fill(this.isPng ? new Color(0, 0, 0, 0) : new Color(255, 255, 255));
	}
	public Image fill(Color color) {
		this.bgColor = color;
		this.graphics.setBackground(color);
		this.graphics.clearRect(0, 0, width, height);
		return this;
	}
	public FontMetrics setFont(Font font) {
		this.hasFont = true;
		this.font = font;
		this.graphics.setFont(font);
		this.fontMetrics = this.graphics.getFontMetrics();
		return this.fontMetrics;
	}
	public FontMetrics setFont(String name, FontStyle style, int size) {
		Font font = new Font(name, style.constant, size);
		return this.setFont(font);
	}
	public FontMetrics setFontSize(int size) {
		return this.setFont(this.font.deriveFont((float)size));
	}
	public Image setLineStyle(Stroke stroke) {
		this.graphics.setStroke(stroke);
		return this;
	}
	
	/**
	 * Returns a new image as a circle. Disposes the current object.
	 * @return
	 */
	public Image circular() {
		Image copy = new Image(this.width, this.height, this.isPng);
		copy.graphics.setClip(new Ellipse2D.Float(0, 0, width, height));
		copy.graphics.drawImage(this.image, 0, 0, null);
		this.dispose();
		return copy;
	}
	public Image round(int size) {
		Image copy = new Image(this.width, this.height, true);
		copy.clear();
		copy.graphics.setClip(new RoundRectangle2D
			.Float(0, 0, width, height, size, size));
		copy.graphics.drawImage(this.image, 0, 0, null);
		this.dispose();
		return copy;
	}
	
	public Image drawRect(Rect rectangle, Color color) {
		this.graphics.setColor(color);
		this.graphics.fillRect(rectangle.x, rectangle.y,
				rectangle.width, rectangle.height);
		return this;
	}
	public Image drawRect(int x, int y, int width, int height, Color color) {
		this.graphics.setColor(color);
		this.graphics.fillRect(x, y, width, height);
		return this;
	}
	public Image drawRectCorners(int x1, int y1, int x2, int y2, Color color) {
		int x = Math.min(x1, x2);
		int y = Math.min(y1, y2);
		int maxX = Math.max(x1, x2);
		int maxY = Math.max(y1, y2);
		int width = maxX - x;
		int height = maxY - y;
		
		this.graphics.setColor(color);
		this.graphics.fillRect(x, y, width, height);
		return this;
	}
	public Image drawRoundedRect(Rect rectangle, int diameter, Color color) {
		this.graphics.setColor(color);
		this.graphics.fillRoundRect(rectangle.x, rectangle.y,
			rectangle.width, rectangle.height, diameter, diameter);
		return this;
	}
	public Image drawRoundedRect(int x, int y, int width, int height, int diameter, Color color) {
		this.graphics.setColor(color);
		this.graphics.fillRoundRect(x, y, width, height, diameter, diameter);
		return this;
	}
	public Image drawRoundedRectCorners(int x1, int y1, int x2, int y2, int diameter, Color color) {
		int x = Math.min(x1, x2);
		int y = Math.min(y1, y2);
		int maxX = Math.max(x1, x2);
		int maxY = Math.max(y1, y2);
		int width = maxX - x;
		int height = maxY - y;
		
		this.graphics.setColor(color);
		this.graphics.fillRoundRect(x, y, width, height, diameter, diameter);
		return this;
	}
	public Image drawOutlinedRect(Rect rectangle, Color color) {
		this.graphics.setColor(color);
		this.graphics.drawRect(rectangle.x, rectangle.y,
				rectangle.width, rectangle.height);
		return this;
	}
	public Image drawOutlinedRect(int x, int y, int width, int height, Color color) {
		this.graphics.setColor(color);
		this.graphics.drawRect(x, y, width, height);
		return this;
	}
	public Image drawOutlinedRectCorners(int x1, int y1, int x2, int y2, Color color) {
		int x = Math.min(x1, x2);
		int y = Math.min(y1, y2);
		int maxX = Math.max(x1, x2);
		int maxY = Math.max(y1, y2);
		int width = maxX - x;
		int height = maxY - y;
		
		this.graphics.setColor(color);
		this.graphics.drawRect(x, y, width, height);
		return this;
	}
	public Image drawOutlinedRoundedRect(Rect rectangle, int diameter, Color color) {
		this.graphics.setColor(color);
		this.graphics.drawRoundRect(rectangle.x, rectangle.y,
			rectangle.width, rectangle.height, diameter, diameter);
		return this;
	}
	public Image drawOutlinedRoundedRect(int x, int y, int width, int height, int diameter, Color color) {
		this.graphics.setColor(color);
		this.graphics.drawRoundRect(x, y, width, height, diameter, diameter);
		return this;
	}
	public Image drawOutlinedRoundedRectCorners(int x1, int y1, int x2, int y2, int diameter, Color color) {
		int x = Math.min(x1, x2);
		int y = Math.min(y1, y2);
		int maxX = Math.max(x1, x2);
		int maxY = Math.max(y1, y2);
		int width = maxX - x;
		int height = maxY - y;
		
		this.graphics.setColor(color);
		this.graphics.drawRoundRect(x, y, width, height, diameter, diameter);
		return this;
	}
	
	public Image drawString(String string, Color color, float x, float y) {
		this.graphics.setColor(color);
		this.graphics.drawString(string, x, y);
		return this;
	}
	public Image drawStringWrapping(String string, Color color, Rect bounds) {
		
		char[] characters = string.toCharArray();
		
		int x = bounds.left;
		int y = bounds.top;
		int width = bounds.width;
		this.graphics.setColor(color);
		
		Map<TextAttribute, ?> attributes = this.font.getAttributes();
		AttributedString str = new AttributedString(string, attributes);
		AttributedCharacterIterator aci = str.getIterator();
		int strStart = aci.getBeginIndex();
		int strEnd = aci.getEndIndex();
		FontRenderContext context = this.graphics.getFontRenderContext();
		LineBreakMeasurer measure = new LineBreakMeasurer(aci, context);
		
		int position = strStart;
		measure.setPosition(position);
		while((position = measure.getPosition()) < strEnd) {
			int endIndex = measure.nextOffset(width);
			
			TextLayout layout = null;
			for(int i = position; i < endIndex; i++)
				if(characters[i] == '\n') {
					layout = measure.nextLayout(width, i + 1, false);
					break;
				}
			
			if(layout == null)
				layout = measure.nextLayout(width);
			
			y += layout.getAscent();
			layout.draw(this.graphics, x, y);
			y += layout.getDescent() - layout.getLeading();
			if(y >= bounds.bottom)
				break;
		}
		return this;
	}
	public Image drawCenteredString(String string, Color color, float x, float y) {
		this.graphics.setColor(color);
		int width = this.fontMetrics.stringWidth(string);
		int height = this.fontMetrics.getAscent()
			- this.fontMetrics.getDescent();
		x -= (width / 2);
		y += (height / 2);
		this.graphics.drawString(string, x, y);
		return this;
	}
	public Image drawCenteredString(String string, Color color, Rect inside) {
		this.graphics.setColor(color);
		int fWidth = this.fontMetrics.stringWidth(string);
		int fHeight = this.fontMetrics.getAscent()
			- this.fontMetrics.getDescent();
		int x = inside.getCenterX();
		int y = inside.getCenterY();
		x -= (fWidth / 2);
		y += (fHeight / 2);
		this.graphics.drawString(string, x, y);
		return null;
	}
	public Image drawCenteredString(String string, Color color) {
		return this.drawCenteredString(string, color, this.bounds);
	}
	
	public Image drawLine(int x1, int y1, int x2, int y2, Color color) {
		this.graphics.setColor(color);
		this.graphics.drawLine(x1, y1, x2, y2);
		return this;
	}
	public Image drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3, Color color) {
		int[] x = new int[] { x1, x2, x3 };
		int[] y = new int[] { y1, y2, y3 };
		return drawPoly(x, y, 3, color);
	}
	public Image drawPoly(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, Color color) {
		int[] x = new int[] { x1, x2, x3, x4 };
		int[] y = new int[] { y1, y2, y3, y4 };
		return drawPoly(x, y, 4, color);
	}
	public Image drawPoly(Point[] points, Color color) {
		int count = points.length;
		int[] x = new int[count];
		int[] y = new int[count];
		for(int i = 0; i < count; i++) {
			x[i] = points[i].x;
			y[i] = points[i].y;
		}
		return drawPoly(x, y, count, color);
	}
	public Image drawPoly(int[] xPoints, int[] yPoints, int pointCount, Color color) {
		this.graphics.setColor(color);
		this.graphics.fillPolygon(xPoints, yPoints, pointCount);
		return this;
	}
	public Image drawOutlinedTriangle(int x1, int y1, int x2, int y2, int x3, int y3, Color color) {
		int[] x = new int[] { x1, x2, x3 };
		int[] y = new int[] { y1, y2, y3 };
		return drawOutlinedPoly(x, y, 3, color);
	}
	public Image drawOutlinedPoly(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, Color color) {
		int[] x = new int[] { x1, x2, x3, x4 };
		int[] y = new int[] { y1, y2, y3, y4 };
		return drawOutlinedPoly(x, y, 4, color);
	}
	public Image drawOutlinedPoly(Point[] points, Color color) {
		int count = points.length;
		int[] x = new int[count];
		int[] y = new int[count];
		for(int i = 0; i < count; i++) {
			x[i] = points[i].x;
			y[i] = points[i].y;
		}
		return drawOutlinedPoly(x, y, count, color);
	}
	public Image drawOutlinedPoly(int[] xPoints, int[] yPoints, int pointCount, Color color) {
		this.graphics.setColor(color);
		this.graphics.drawPolygon(xPoints, yPoints, pointCount);
		return this;
	}
	
	public Image fillImage(Image other) {
		return this.drawImage(other, 0, 0, this.width, this.height);
	}
	public Image fillImageKeepAspect(Image other) {
		return this.drawImageKeepAspect(other, 0, 0, this.width, this.height);
	}
	public Image drawImage(Image other, int x, int y) {
		this.graphics.drawImage(other.image, null, x, y);
		return this;
	}
	public Image drawImage(Image other, int x, int y, int newWidth, int newHeight) {
		this.graphics.drawImage(other.image, x, y, newWidth, newHeight, null);
		return this;
	}
	public Image drawImageKeepAspect(Image other, int x, int y, float targetWidth, float targetHeight) {
		float otherWidth = other.width;
		float otherHeight = other.height;
		
		float srcSmallest = Math.min(otherWidth, otherHeight);
		float trgLargest = Math.max(targetWidth, targetHeight);
		float scaleFactor = trgLargest / srcSmallest;
		
		float _newWidth = otherWidth * scaleFactor;
		float _newHeight = otherHeight * scaleFactor;
		int newWidth = ((int)_newWidth) + 1;
		int newHeight = ((int)_newHeight) + 1;
		
		int roomX = newWidth - (int)targetWidth;
		int roomY = newHeight - (int)targetHeight;
		x -= roomX >> 1;
		y -= roomY >> 1;
		
		return this.drawImage(other, x, y, newWidth, newHeight);
	}
}