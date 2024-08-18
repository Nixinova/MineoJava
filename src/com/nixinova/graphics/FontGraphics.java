package com.nixinova.graphics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class FontGraphics {

	private static String FONT_FILE = "font";
	private static String FONT_SET = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
	private static int CHAR_WIDTH = 6;
	private static int CHAR_HEIGHT = 8;

	private Map<Character, BufferedImage> charImages;

	public FontGraphics() {
		this.charImages = new HashMap<>();
	}

	public void load(double size) {
		Render fontTex = Texture.loadTexture(FONT_FILE, size);
		BufferedImage fontImage = fontTex.getBufferedImage();

		int charWidth = (int) (CHAR_WIDTH * size);
		int charHeight = (int) (CHAR_HEIGHT * size);

		final int offset = (int) (1 * size);

		for (int i = 0; i < FONT_SET.length(); i++) {
			int x = i * charWidth + offset;
			int y = offset;
			char ch = FONT_SET.charAt(i);
			BufferedImage chImg = fontImage.getSubimage(x, y, charWidth, charHeight);
			this.charImages.put(ch, trimTransparent(chImg, size));
		}
	}

	public void drawString(Graphics graphics, String text, int x, int y) {
		int xOffset = 0;
		for (char c : text.toCharArray()) {
			BufferedImage chImg = this.charImages.get(c);
			if (chImg != null) {
				graphics.drawImage(chImg, x + xOffset, y, null);
				xOffset += chImg.getWidth();
			}
		}
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

}
