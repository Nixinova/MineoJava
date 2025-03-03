package com.nixinova.mineo.ui.graphics;

import java.awt.image.BufferedImage;

public class Render {
	public final int width;
	public final int height;

	private int[] pixels;

	public Render(int width, int height) {
		this.width = width;
		this.height = height;
		this.pixels = new int[width * height];
	}

	public void draw(Render render, int xOffset, int yOffset) {
		for (int y = 0; y < render.height; y++) {
			int yPx = y + yOffset;
			if (yPx >= 0 && yPx < this.height) {
				for (int x = 0; x < render.width; x++) {
					int xPx = x + xOffset;
					if (xPx >= 0 && xPx < this.width) {
						int alpha = render.pixels[x + y * render.width];
						this.pixels[getPixelIndex(xPx, yPx)] = alpha;
					}
				}
			}
		}
	}

	public void fill(int startX, int startY, int width, int height, int color) {
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				int pixelI = getPixelIndex(startX + x, startY + y);
				if (isValidPosition(startX + x, startY + y)) {
					this.pixels[pixelI] = color;
				}
			}
	}

	public boolean isValidPosition(int screenX, int screenY) {
		boolean widthValid = screenX >= 0 && screenX < this.width;
		boolean heightValid = screenY >= 0 && screenY < this.height;
		int pixelI = this.getPixelIndex(screenX, screenY);
		boolean indexValid = pixelI >= 0 && pixelI < this.imageSize();
		return widthValid && heightValid && indexValid;
	}

	public BufferedImage getBufferedImage() {
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		bufferedImage.setRGB(0, 0, width, height, getImage(), 0, width);
		return bufferedImage;
	}

	public int getPixel(int pixelI) {
		return this.pixels[pixelI];
	}

	public int getPixel(int screenX, int screenY) {
		return this.pixels[getPixelIndex(screenX, screenY)];
	}

	public void setPixel(int pixelI, int color) {
		this.pixels[pixelI] = color;
	}

	public void setPixel(int screenX, int screenY, int color) {
		this.pixels[getPixelIndex(screenX, screenY)] = color;
	}

	public int[] getImage() {
		return this.pixels;
	}

	public void replaceImage(int[] pixels) {
		this.pixels = pixels;
	}

	public void clearImage() {
		this.pixels = new int[this.width * this.height];
	}

	public int getPixelIndex(int screenX, int screenY) {
		int pixelI = screenX + (screenY * this.width);
		return pixelI;
	}

	public int imageSize() {
		return this.pixels.length;
	}
}
