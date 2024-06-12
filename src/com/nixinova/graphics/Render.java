package com.nixinova.graphics;

import com.nixinova.input.Game;

public class Render extends Game {
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
}
