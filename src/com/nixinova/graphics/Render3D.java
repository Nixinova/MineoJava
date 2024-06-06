package com.nixinova.graphics;

import com.nixinova.input.Controller;
import com.nixinova.main.Game;
import com.nixinova.readwrite.Options;

public class Render3D extends Render {
	public double[] zBuffer;

	public double renderDist = Options.renderDistance;

	public static double playerX = 0.0D;
	public static double playerY = 0.0D;
	public static double playerZ = 0.0D;

	public static final double GROUND = Options.groundHeight;
	public static final double SKY = Options.skyHeight;

	public Render3D(int width, int height) {
		super(width, height);
		this.zBuffer = new double[width * height];
	}

	public void floor(Game game) {
		double xMove = Game.controls.x;
		double yMove = Game.controls.y;
		double zMove = Game.controls.z;
		double bobbing = Math.sin(Game.time / 1.0D) * 10.5D;

		double rotation = Game.controls.rot;
		double cosine = Math.cos(rotation);
		double sine = Math.sin(rotation);

		for (int y = 0; y < this.height; y++) {
			double sky = (y - this.height / 2.0D) / this.height;

			double z = (GROUND + yMove) / sky;
			if (Controller.walking) {
				z = (GROUND + yMove) / sky + bobbing;
			}

			if (sky < 0.0D) {
				z = (SKY - yMove) / -sky;
				if (Controller.walking) {
					z = (SKY - yMove - bobbing) / -sky;
				}
			}

			for (int x = 0; x < this.width; x++) {
				double depth = (x - this.width / 2.0D) / this.height;
				depth *= z;
				double xx = depth * cosine + z * sine + xMove;
				double yy = z * cosine - depth * sine + zMove;
				int xPx = (int) (xx + xMove);
				int yPx = (int) (yy + zMove);

				this.zBuffer[x + y * this.width] = z;

				if (z < this.renderDist / 2.0D) {
					if (z <= SKY / -sky + 10.0D) {
						this.pixels[x + y * this.width] = Textures.sky.pixels[(xPx & 0x7) + (yPx & 0x7) * 8];
					} else {
						this.pixels[x + y * this.width] = Textures.grass.pixels[(xPx & 0x7) + (yPx & 0x7) * 8];
					}
				}

				playerX = xMove;
				playerY = yMove;
				playerZ = zMove;
			}
		}
	}

	public void renderDistLimiter() {
		for (int i = 0; i < this.width * this.height; i++) {
			int colour = this.pixels[i];
			int brightness = (int) (this.renderDist * 4.0D / this.zBuffer[i]);

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
