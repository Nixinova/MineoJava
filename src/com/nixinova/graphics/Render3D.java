package com.nixinova.graphics;

import java.util.Arrays;
import java.util.stream.IntStream;

import com.nixinova.input.Controller;
import com.nixinova.main.Game;
import com.nixinova.main.Mineo;
import com.nixinova.readwrite.Options;
import com.nixinova.types.BlockCoord;
import com.nixinova.world.Block;
import com.nixinova.world.Blocks;

public class Render3D extends Render {

	public double[] zBuffer;

	private boolean fogAlrApplied;
	private double lastXMove, lastYMove, lastZMove, lastRot, lastTilt;
	private boolean[] lastKbdInput;

	public Render3D(int width, int height) {
		super(width, height);
		this.zBuffer = new double[width * height];
		this.fogAlrApplied = false;
		this.lastXMove = this.lastYMove = this.lastZMove = this.lastRot = this.lastTilt = 0;
	}

	public void renderWorld(Game game) {
		final int TEX_SIZE = Textures.PX_PER_BLOCK;

		double xMove = game.controls.x;
		double yMove = game.controls.y;
		double zMove = game.controls.z;
		double bobbing = Math.sin(game.time) / 10.0;

		double rotation = game.controls.rot;
		double cosine = Math.cos(rotation);
		double sine = Math.sin(rotation);

		double tilt = game.controls.tilt;
		double tiltCosine = Math.cos(tilt);
		double tiltSine = Math.sin(tilt);

		// Early return if player hasn't inputted this tick
		IntStream indicesRange = IntStream.range(0, this.lastKbdInput != null ? this.lastKbdInput.length : 0);
		boolean hasntPressed = indicesRange.allMatch(i -> game.kbdInput[i] == this.lastKbdInput[i]); // check all array items are equal
		boolean hasntMoved = xMove == lastXMove && yMove == lastYMove && zMove == lastZMove;
		boolean hasntLooked = rotation == lastRot && tilt == lastTilt;
		if (hasntPressed && hasntMoved && hasntLooked) {
			return;
		}

		this.fogAlrApplied = false; // reinitialise distance limiter as we are rerendering screen

		// Loop through pixel rows
		for (int y = 0; y < this.height; y++) {
			// Relative vertical position
			double vert = (y - this.height / 2.0D) / this.height;
			vert = vert * tiltCosine - tiltSine; // apply tilt

			// Calculate depth
			double depth;
			if (vert < 0) {
				// If sky
				depth = (Options.skyHeight - yMove) / -vert;
				if (game.controls.isWalking) {
					depth = (Options.skyHeight - yMove - bobbing) / -vert;
				}
			} else {
				// If ground
				depth = (Options.groundHeight + yMove) / vert;
				if (game.controls.isWalking) {
					depth = (Options.groundHeight + yMove) / vert + bobbing;
				}
			}

			// Loop through pixel columns
			for (int x = 0; x < this.width; x++) {
				int pixelI = x + (y * this.width);

				// Relative horizontal position
				double horiz = (x - this.width / 2.0D) / this.height;
				horiz *= depth; // apply depth scale

				// World pixel coords
				int pxX = (int) (horiz * cosine + depth * sine + xMove);
				//int pxY = (int) (depth * vert + yMove);
				int pxZ = (int) (depth * cosine - horiz * sine + zMove);
				
				// World block coords
				BlockCoord blockCoord = Blocks.worldPxToBlockCoords(pxX, 0, pxZ);
				int blockX = blockCoord.x;
				int blockY = blockCoord.y;
				int blockZ = blockCoord.z;

				// Set looking at block
				if (pixelI == width * height / 2) {
					Mineo.world.setLookingAt(blockX, blockY, blockZ);
				}

				// Store depth in Z buffer
				this.zBuffer[pixelI] = depth;

				// Get texture for block at this coordinate if within render distance
				Render texture = Block.SKY.getTexture();
				boolean withinRenderDist = depth < Options.renderDistance;
				boolean isNotSky = depth > Options.skyHeight / -vert;
				if (withinRenderDist && isNotSky) {
					// Render block
					texture = Mineo.world.getTextureAt(blockX, blockY, blockZ);
				}
				// Apply texture
				int texPx = (pxX & (TEX_SIZE - 1)) + (pxZ & (TEX_SIZE - 1)) * TEX_SIZE;
				this.pixels[pixelI] = texture.pixels[texPx];
			}
		}

		// Mouse cursor
		this.drawCursor();

		// Set last control moves
		this.lastXMove = xMove;
		this.lastYMove = yMove;
		this.lastZMove = zMove;
		this.lastRot = rotation;
		this.lastTilt = tilt;
		this.lastKbdInput = Arrays.copyOf(game.kbdInput, game.kbdInput.length);
	}

	/** Adds depth-based fog to the pixels */
	public void renderDistLimiter() {
		double gamma = Options.gamma;

		if (this.fogAlrApplied)
			return;

		for (int i = 0; i < this.width * this.height; i++) {
			// Get pixel colour
			int colour = this.pixels[i];
			// Determine brightness based on depth value from Z buffer
			int brightness = (int) (Options.renderDistance * gamma / this.zBuffer[i]);

			// Clamp brightness
			if (brightness < 0)
				brightness = 0;
			if (brightness > 0xFF)
				brightness = 0xFF;

			// Calculate final RGB from pixel colour + fog
			int r = colour >> 16 & 0xFF;
			int g = colour >> 8 & 0xFF;
			int b = colour & 0xFF;
			r = r * brightness / 255;
			g = g * brightness / 255;
			b = b * brightness / 255;

			// Save fog-adjusted colour to pixel
			this.pixels[i] = r << 16 | g << 8 | b;
			this.fogAlrApplied = true;
		}
	}

	private void drawCursor() {
		int size = 5;
		int startX = (width - size) / 2;
		int startY = (height - size) / 2;
		for (int x = startX; x < startX + size; x++)
			for (int y = startY; y < startY + size; y++)
				pixels[x + (y * width)] = 0;
	}
}
