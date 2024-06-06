package com.nixinova.graphics;

import com.nixinova.main.Game;

public class Render3D extends Render {
	public Render3D(int width, int height) {
		super(width, height);
	}

	public void floor(Game game) {
		double floorPos = 8.0D;
		double ceilPos = 128.0D;
		double forward = Game.controls.z;
		double right = Game.controls.x;
		double rotation = Game.controls.rot;
		double cosine = Math.cos(rotation);
		double sine = Math.sin(rotation);
		
		for (int y = 0; y < this.height; y++) {
			double ceiling = (y - this.height / 2.0D) / this.height;
			double z = floorPos / ceiling;

			for (int x = 0; x < this.width; x++) {
				double depth = (x - this.width / 2.0D) / this.height;
				depth *= z;
				double xx = depth * cosine + z * sine + right;
				double yy = z * cosine - depth * sine + forward;
				int xPx = (int) (xx + right);
				int yPx = (int) (yy + forward);
				this.pixels[x + y * this.width] = xPx * 16 | yPx * 16 << 8;
			}
		}
	}
}
