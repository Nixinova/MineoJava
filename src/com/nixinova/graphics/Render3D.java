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

		double tilt = Game.controls.tilt;
		double tiltCosine = Math.cos(tilt);
		double tiltSine = Math.sin(tilt);

		// Loop through pixel rows
		for (int y = 0; y < this.height; y++) {
			// Relative sky position
			double sky = (y - this.height / 2.0D) / this.height;
			// Apply tilt to sky
			sky = sky * tiltCosine - tiltSine;

			// Calculate depth
			double z;
			if (sky < 0.0D) {
				// If sky
				z = (Options.skyHeight - yMove) / -sky;
				if (Controller.walking) {
					z = (Options.skyHeight - yMove - bobbing) / -sky;
				}
			} else {
				// If ground
				z = (Options.groundHeight + yMove) / sky;
				if (Controller.walking) {
					z = (Options.groundHeight + yMove) / sky + bobbing;
				}
			}

			// Loop through pixel columns
			for (int x = 0; x < this.width; x++) {
				int pixelI = x + y * this.width;

				double depth = (x - this.width / 2.0D) / this.height;
				depth *= z;
				double newX = depth * cosine + z * sine + xMove;
				double newY = z * cosine - depth * sine + zMove;
				int xPx = (int) (newX + xMove);
				int yPx = (int) (newY + zMove);

				// Store depth in Z buffer
				this.zBuffer[pixelI] = z;

				if (z < this.renderDist && z > Options.skyHeight / -sky) {
					// Apply grass texture within render distance
					this.pixels[pixelI] = Textures.grass.pixels[(xPx & 0x7) + (yPx & 0x7) * 8];
				} else {
					// Apply sky texture otherwise
					this.pixels[pixelI] = Textures.sky.pixels[(xPx & 0x7) + (yPx & 0x7) * 8];
				}

				playerX = xMove;
				playerY = yMove;
				playerZ = zMove;
			}
		}
	}

	/** Adds depth-based fog to the pixels */
	public void renderDistLimiter() {
		double gamma = Options.gamma;

		for (int i = 0; i < this.width * this.height; i++) {
			// Get pixel colour
			int colour = this.pixels[i];
			// Determine brightness based on depth value from Z buffer
			int brightness = (int) (this.renderDist * gamma / this.zBuffer[i]);

			// Clamp brightness
			if (brightness < 0)
				brightness = 0;
			if (brightness > 255)
				brightness = 255;

			// Calculate final RGB from pixel colour + fog
			int r = colour >> 16 & 0xFF;
			int g = colour >> 8 & 0xFF;
			int b = colour & 0xFF;
			r = r * brightness / 255 + 8;
			g = g * brightness / 255;
			b = b * brightness / 255;

			// Save fog-adjusted colour to pixel
			this.pixels[i] = r << 16 | g << 8 | b;
		}
	}
}
