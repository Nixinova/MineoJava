package com.nixinova.main;

public class Render3D extends Render {
	double time;

	public Render3D(int width, int height) {
		super(width, height);
		this.time = 0.0D;
	}

	public void floor(Game game) {
		for (int y = 0; y < this.height; y++) {
			double ceiling = Math.abs((y - this.height / 2.0D) / this.height);
			double z = 8.0D / ceiling;
			this.time += 5.0E-4D;

			for (int x = 0; x < this.width; x++) {
				double depth = x - (x - this.width / 2.0D) / this.height;
				depth *= z;
				double xx = depth;
				double yy = z + this.time;
				int xPx = (int) xx;
				int yPx = (int) yy;
				this.pixels[x + y * this.width] = (xPx & 0xF) * 16 | (yPx & 0xF) * 16 << 8;
			}
		}
	}
}
