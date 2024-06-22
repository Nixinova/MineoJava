package com.nixinova.graphics;

import java.util.Arrays;
import java.util.stream.IntStream;

import com.nixinova.main.Game;
import com.nixinova.main.Mineo;
import com.nixinova.main.Player;
import com.nixinova.readwrite.Options;
import com.nixinova.types.BlockCoord;
import com.nixinova.types.Conversion;
import com.nixinova.types.Coord;
import com.nixinova.world.Block;
import com.nixinova.world.World;

public class Render3D extends Render {

	public double[] depthStore;

	private boolean fogAlrApplied;
	private double lastXMove, lastYMove, lastZMove, lastGround, lastRot, lastTilt;
	private boolean[] lastKbdInput;

	public Render3D(int width, int height) {
		super(width, height);
		this.depthStore = new double[width * height];
		this.fogAlrApplied = false;
		this.lastXMove = this.lastYMove = this.lastZMove = this.lastRot = this.lastTilt = 0;
	}

	public void renderWorld(Game game) {
		final int TEX_SIZE = Conversion.PX_PER_BLOCK;

		
		Coord pos = game.controls.getControllerCoords();
		double bobbing = Math.sin(game.time) / 10.0;
		double rotation = game.controls.getXRot();
		double rotCos = Math.cos(rotation);
		double rotSin = Math.sin(rotation);
		double tilt = game.controls.getYRot();
		double tiltCos = Math.cos(tilt);
		double tiltSin = Math.sin(tilt);

		// Early return if player hasn't inputted this tick
		IntStream indicesRange = IntStream.range(0, this.lastKbdInput != null ? this.lastKbdInput.length : 0);
		boolean hasntPressed = indicesRange.allMatch(i -> game.kbdInput[i] == this.lastKbdInput[i]); // check all array items are equal
		boolean hasntMoved = pos.x == lastXMove && pos.y == lastYMove && pos.z == lastZMove && lastGround == game.controls.playerGround;
		boolean hasntLooked = rotation == lastRot && tilt == lastTilt;
		if (hasntPressed && hasntMoved && hasntLooked) {
			return;
		}

		this.fogAlrApplied = false; // reinitialise distance limiter as we are rerendering screen

		// Loop through pixel rows
		for (int yPx = 0; yPx < this.height; yPx++) {
			// Relative vertical position of screen pixel
			double vert = (yPx - this.height / 2.0D) / this.height;
			vert = vert * tiltCos - tiltSin; // apply tilt

			boolean isSky = vert < 0; // top half of screen is sky
			boolean inVoid = pos.y < 0;
			boolean generateSky = isSky && !inVoid;

			// Loop through pixel columns
			for (int xPx = 0; xPx < this.width; xPx++) {
				int pixelI = xPx + (yPx * this.width);

				// Relative horizontal position of screen pixel
				double horiz = (xPx - this.width / 2.0D) / this.height;

				// Depth calculation
				double offset = pos.y;
				double skyDepth = (World.SKY_Y_PX - offset) / -vert;
				double worldDepth = (Player.PLAYER_HEIGHT + offset) / vert;
				worldDepth +=  (game.controls.playerGround - (int) game.controls.playerGround)*10; // world layer falling through effect
				double depth = generateSky ? skyDepth : worldDepth;
				if (game.controls.isWalking) {
					depth += bobbing;
				}
				// Save to depth buffer
				this.depthStore[pixelI] = isSky ? skyDepth : worldDepth;

				// Apply depth
				horiz *= depth;

				// World pixel coords
				int texelX = (int) (horiz * rotCos + depth * rotSin + pos.x);
				int texelY = 0; // (int) (depth * vert + pos.y);
				int texelZ = (int) (depth * rotCos - horiz * rotSin + pos.z);

				// World block coords
				BlockCoord blockCoord = Conversion.worldPxToBlockCoords(texelX, texelY, texelZ);
				int blockX = blockCoord.x;
				int blockY = (int) game.controls.playerGround;
				int blockZ = blockCoord.z;

				// Set looking at block
				if (pixelI == width * height / 2) {
					Mineo.player.setLookingAt(blockX, blockY, blockZ);
				}

				// Get texture for block at this coordinate if within render distance
				Render texture = Block.SKY.getTexture();
				boolean withinRenderDist = depth < Options.renderDistance;
				if (withinRenderDist && !generateSky) {
					// Render block
					texture = Mineo.world.getTextureAt(blockX, blockY, blockZ);
				}
				// Apply texture
				int texPx = (texelX & (TEX_SIZE - 1)) + (texelZ & (TEX_SIZE - 1)) * TEX_SIZE;
				this.pixels[pixelI] = texture.pixels[texPx];
			}
		}

		// Mouse cursor
		this.drawCursor();

		// Apply render distance limiter
		this.renderDistLimiter();

		// Set last control moves
		this.lastXMove = pos.x;
		this.lastYMove = pos.y;
		this.lastZMove = pos.z;
		this.lastGround = game.controls.playerGround;
		this.lastRot = rotation;
		this.lastTilt = tilt;
		this.lastKbdInput = Arrays.copyOf(game.kbdInput, game.kbdInput.length);

	}

	/** Adds depth-based fog to the pixels */
	private void renderDistLimiter() {
		double gamma = Options.gamma;

		if (this.fogAlrApplied)
			return;

		for (int i = 0; i < this.width * this.height; i++) {
			// Get pixel colour
			int colour = this.pixels[i];
			// Determine brightness based on depth value from Z buffer
			int brightness = (int) (Options.renderDistance * gamma / this.depthStore[i]);

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
