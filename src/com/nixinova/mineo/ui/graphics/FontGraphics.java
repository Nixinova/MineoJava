package com.nixinova.mineo.ui.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.util.HashMap;
import java.util.Map;

public class FontGraphics {
	// Pre-cache common font sizes
	public static final FontGraphics FONT_100 = new FontGraphics(1.0);
	public static final FontGraphics FONT_150 = new FontGraphics(1.5);
	public static final FontGraphics FONT_200 = new FontGraphics(2.0);
	public static final FontGraphics FONT_600 = new FontGraphics(6.0);
	public static final FontGraphics FONT_800 = new FontGraphics(8.0);

	private static final String FONT_FILE = "font";
	private static final String FONT_SET = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~©";
	private static final int CHAR_WIDTH = 6;
	private static final int CHAR_HEIGHT = 9;

	private static boolean warnPrinted = false;

	private double scale;
	private Map<Character, BufferedImage> charImages;
	private Map<Character, Integer> charWidths;
	private BufferedImage baseFontImage;

	public FontGraphics(double scale) {
		this.scale = scale;
		this.charImages = new HashMap<>();
		this.charWidths = new HashMap<>();
		this.baseFontImage = Texture.loadImage(FONT_FILE);
		load();
	}

	public void load() {
		try {
			BufferedImage fontImage = this.baseFontImage;
			if (this.scale != 1) {
				fontImage = Texture.scaleImage(fontImage, this.scale);
			}

			int charWidth = (int) (CHAR_WIDTH * this.scale);
			int charHeight = (int) (CHAR_HEIGHT * this.scale);

			final int offset = (int) (1 * this.scale);

			for (int i = 0; i < FONT_SET.length(); i++) {
				int x = i * charWidth + offset;
				int y = offset;
				char ch = FONT_SET.charAt(i);
				BufferedImage chImg = fontImage.getSubimage(x, y, charWidth, charHeight);
				this.charImages.put(ch, trimTransparent(ch, chImg));
			}
		} catch (RasterFormatException e) {
			if (warnPrinted)
				return;
			warnPrinted = true;

			System.err.println(String.format("Warning: Font file %s.png is malformed.", FONT_FILE));
			System.out.println("Font file must contain exactly these characters in this order:" + FONT_SET);
			System.out.println(String.format("Font characters must be exactly %dx%dpx.", CHAR_WIDTH, CHAR_HEIGHT));
		}
	}

	public void drawString(Graphics graphics, String text, Color colour, int x, int y) {
		int xOffset = 0;
		for (char c : text.toCharArray()) {
			BufferedImage chImg = this.charImages.get(c);
			chImg = applyCol(chImg, colour);
			if (chImg != null) {
				graphics.drawImage(chImg, x + xOffset, y, null);
				xOffset += chImg.getWidth();
			}
		}
	}

	public void drawStringOutlined(Graphics graphics, String text, int startX, int startY, TextColorScheme scheme) {
		int size = (int) this.scale;
		for (int x = -size; x <= size; x += size) {
			for (int y = -size; y <= size; y += size) {
				drawString(graphics, text, scheme.border, startX + x, startY + y);
			}
		}
		drawString(graphics, text, scheme.text, startX, startY);
	}

	public int getTextLength(String text) {
		int totalLength = 0;
		for (char ch : text.toCharArray()) {
			int width = charWidths.get(ch);
			totalLength += width;
		}
		return (int) (totalLength);
	}

	private BufferedImage trimTransparent(char ch, BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();

		final int minSpace = 2 * (int) this.scale;
		int trimX = width;

		// right to left to find first non-transparent column
		for (int x = width - minSpace; x >= 0; x--) {
			boolean allTransparent = true;

			for (int y = 0; y < height; y++) {
				int pixel = image.getRGB(x, y);
				// check alpha channel for transparency
				if ((pixel >> 24) != 0) {
					allTransparent = false;
					break;
				}
			}
			// set trim X value if there is a solid pixel
			if (!allTransparent) {
				trimX = x + minSpace;
				break;
			}
		}

		// set char width
		charWidths.put(ch, trimX);

		// return cropped image
		return image.getSubimage(0, 0, trimX, height);
	}

	private BufferedImage applyCol(BufferedImage image, Color col) {
		int colour = col.getRGB();
		int width = image.getWidth();
		int height = image.getHeight();

		BufferedImage newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = image.getRGB(x, y);
				if (pixel == -1)
					newImg.setRGB(x, y, colour);
			}

		}

		return newImg;
	}

}
