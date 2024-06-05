package com.nixinova.main;

public class Render3D extends Render {
	double time;

	public Render3D(int width, int height) {
		super(width, height);
		this.time = 0.0D;
	}

	public void floor(Game game) {
		double floorPos = 8.0D;
		double ceilPos = 160.0D;
		double rotation = Game.time / 100.0D;
		double cosine = Math.cos(rotation);
		double sine = Math.sin(rotation);
		for (int y = 0; y < this.height; y++) {
			double ceiling = (y - this.height / 2.0D) / this.height;
			double z = floorPos / ceiling;
			if (ceiling < 0.0D)
				z = ceilPos / -ceiling;
			for (int x = 0; x < this.width; x++) {
				double depth = x - (x - this.width / 2.0D) / this.height;
				depth *= z;
				double xx = depth * cosine + z * sine;
				double yy = z * cosine - depth * sine;
				int xPx = (int) xx;
				int yPx = (int) yy;
				this.pixels[x + y * this.width] = (xPx & 0xF) * 16 | (yPx & 0xF) * 16 << 8;
			}
		}
	}
}
