package com.nixinova.graphics;

import java.util.HashMap;
import java.util.Random;

import com.nixinova.input.Controller;
import com.nixinova.input.Game;
import com.nixinova.main.Display;
import com.nixinova.main.Mineo;
import com.nixinova.readwrite.Options;

public class Render3D extends Render {
	public static final int TEX_SIZE = 8;

	public double[] zBuffer;

	public Render3D(int width, int height) {
		super(width, height);
		this.zBuffer = new double[width * height];
	}

	public void floor(Game game) {
		double xMove = Game.controls.x;
		double yMove = Game.controls.y;
		double zMove = Game.controls.z;
		double bobbing = Math.sin(Game.time) / 10.0;

		double rotation = Game.controls.rot;
		double cosine = Math.cos(rotation);
		double sine = Math.sin(rotation);

		double tilt = Game.controls.tilt;
		double tiltCosine = Math.cos(tilt);
		double tiltSine = Math.sin(tilt);

		// Loop through pixel rows
		for (int y = 0; y < this.height; y++) {
			double sky = (y - this.height / 2.0D) / this.height;
			// Apply tilt to sky
			sky = sky * tiltCosine - tiltSine;

			// Calculate depth
			double d;
			if (sky < 0.0D) {
				// If sky
				d = (Options.skyHeight - yMove) / -sky;
				if (Controller.walking) {
					d = (Options.skyHeight - yMove - bobbing) / -sky;
				}
			} else {
				// If ground
				d = (Options.groundHeight + yMove) / sky;
				if (Controller.walking) {
					d = (Options.groundHeight + yMove) / sky + bobbing;
				}
			}

			// Loop through pixel columns
			for (int x = 0; x < this.width; x++) {
				int pixelI = x + (y * this.width);

				double depth = (x - this.width / 2.0D) / this.height * d;
				int pxX = (int) Math.round(depth * cosine + d * sine + xMove);
				int pxZ = (int) Math.round(d * cosine - depth * sine + zMove);
				int blockX = pxX / TEX_SIZE;
				int blockZ = pxZ / TEX_SIZE;

				// Store depth in Z buffer
				this.zBuffer[pixelI] = d;

				int texPx = (pxX & (TEX_SIZE - 1)) + (pxZ & (TEX_SIZE - 1)) * TEX_SIZE;

				Render texture;
				// Apply block texture if in render distance or sky otherwise
				if (d < Options.renderDistance && d > Options.skyHeight / -sky) {
					texture = Textures.grass;
					if (blockX == 0 || blockZ == 0) {
						// World center
						texture = Textures.bedrock;
					} else {
						// Random texture for block
						texture = Mineo.world.getTextureAt(blockX, blockZ);
					}
				} else {
					texture = Textures.sky;
				}
				this.pixels[pixelI] = texture.pixels[texPx];
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
			int brightness = (int) (Options.renderDistance * gamma / this.zBuffer[i]);

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
