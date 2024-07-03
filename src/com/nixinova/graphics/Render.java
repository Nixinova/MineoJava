package com.nixinova.graphics;

import com.nixinova.Conversion;

public class Render {
	public final int width;
	public final int height;
	public final int[] pixels;

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
						this.pixels[xPx + yPx * this.width] = alpha;
					}
				}
			}
		}
	}

	public void fill(int startX, int startY, int width, int height, int color) {
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				int pixelI = (startX + x) + (startY + y) * this.width;
				this.pixels[pixelI] = color;
			}
	}

	public void drawTextureOnScreen(Render texture, int size, int startX, int startY) {
		final int TEX_SIZE = Conversion.PX_PER_BLOCK;
		for (int x = 0; x < size; x++)
			for (int y = 0; y < size; y++) {
				int pixelI = (startX + x) + ((startY + y) * this.width);
				int texelX = x * TEX_SIZE / size;
				int texelY = y * TEX_SIZE / size;
				this.pixels[pixelI] = Texture.getTexel(texture, texelX, texelY);
			}
	}
}
