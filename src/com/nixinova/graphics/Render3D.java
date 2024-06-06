package com.nixinova.graphics;

import com.nixinova.main.Game;

public class Render3D extends Render {
	public double[] zBuffer;
	public double renderDist;

	public Render3D(int width, int height) {
		super(width, height);
		this.renderDist = 5000.0D;
		this.zBuffer = new double[width * height];
	}

	public void floor(Game game) {
		double floorPos = 8.0D;
		double ceilPos = 564.0D;
		double forward = Game.controls.z;
		double right = Game.controls.x;
		double rotation = Game.controls.xRot;
		double cosine = Math.cos(rotation);
		double sine = Math.sin(rotation);

		for (int y = 0; y < this.height; y++) {
			double ceiling = (y - this.height / 2.0D) / this.height;
			double z = floorPos / ceiling;

			if (ceiling < 0.0D)
				z = ceilPos / -ceiling;

			for (int x = 0; x < this.width; x++) {
				double depth = (x - this.width / 2.0D) / this.height;
				depth *= z;
				double xx = depth * cosine + z * sine + right;
				double yy = z * cosine - depth * sine + forward;
				int xPx = (int) (xx + right);
				int yPx = (int) (yy + forward);

				this.zBuffer[x + y * this.width] = z;

				if (z < this.renderDist / 4.0D) {
					if (z == ceilPos / -ceiling) {
						this.pixels[x + y * this.width] = Textures.ceiling.pixels[(xPx & 0x7) + (yPx & 0x7) * 8];
					} else {
						this.pixels[x + y * this.width] = Textures.floor.pixels[(xPx & 0x7) + (yPx & 0x7) * 8];
					}
				}
			}
		}
	}

	public void renderDistLimiter() {
		for (int i = 0; i < this.width * this.height; i++) {
			int colour = this.pixels[i];
			int brightness = (int) (this.renderDist * 2.0D / this.zBuffer[i]);

			if (brightness < 0)
				brightness = 0;
			if (brightness > 255)
				brightness = 255;

			int r = colour >> 16 & 0xFF;
			int g = colour >> 8 & 0xFF;
			int b = colour & 0xFF;
			r = r * brightness / 255 + 8;
			g = g * brightness / 255;
			b = b * brightness / 255;

			this.pixels[i] = r << 16 | g << 8 | b;
		}
	}
}
