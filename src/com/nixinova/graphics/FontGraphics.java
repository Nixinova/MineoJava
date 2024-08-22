package com.nixinova.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.util.HashMap;
import java.util.Map;

public class FontGraphics {
	private static boolean warnPrinted = false;

	private static String FONT_FILE = "font";
	private static String FONT_SET = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~©";
	private static int CHAR_WIDTH = 6;
	private static int CHAR_HEIGHT = 9;

	private Graphics graphics;
	private Map<Character, BufferedImage> charImages;
	private BufferedImage baseFontImage;

	public FontGraphics() {
		this.charImages = new HashMap<>();
		this.baseFontImage = Texture.loadImage(FONT_FILE);
		load(1);
	}

	public void setGraphics(Graphics graphics) {
		this.graphics = graphics;
	}

	public void load(double scale) {
		try {
			BufferedImage fontImage = baseFontImage;
			if (scale != 1) {
				fontImage = Texture.scaleImage(fontImage, scale);
			}

			int charWidth = (int) (CHAR_WIDTH * scale);
			int charHeight = (int) (CHAR_HEIGHT * scale);

			final int offset = (int) (1 * scale);

			for (int i = 0; i < FONT_SET.length(); i++) {
				int x = i * charWidth + offset;
				int y = offset;
				char ch = FONT_SET.charAt(i);
				BufferedImage chImg = fontImage.getSubimage(x, y, charWidth, charHeight);
				this.charImages.put(ch, trimTransparent(chImg, scale));
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

	public void drawString(String text, Color colour, int x, int y) {
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

	public void drawStringOutlined(String text, int startX, int startY, TextColorScheme scheme, int scale) {
		for (int x = -scale; x <= scale; x += scale) {
			for (int y = -scale; y <= scale; y += scale) {
				drawString(text, scheme.border, startX + x, startY + y);
			}
		}
		drawString(text, scheme.text, startX, startY);
	}

	private BufferedImage trimTransparent(BufferedImage image, double size) {
		int width = image.getWidth();
		int height = image.getHeight();

		final int minSpace = (int) (2 * size);
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
