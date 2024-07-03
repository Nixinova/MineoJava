package com.nixinova.graphics;

import java.util.Arrays;
import java.util.stream.IntStream;

import com.nixinova.Conversion;
import com.nixinova.coords.BlockCoord;
import com.nixinova.coords.Coord;
import com.nixinova.coords.PxCoord;
import com.nixinova.main.Game;
import com.nixinova.options.Options;
import com.nixinova.player.Player;
import com.nixinova.world.Block;
import com.nixinova.world.World;

public class Render3D extends Render {

	private double[] depthStore;
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
		double renderDistPx = Coord.fromBlock(Options.renderDistance).toPx().value();

		PxCoord pos = game.controls.getControllerPosition().toPx();
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
		boolean hasntMoved = pos.x == lastXMove && pos.y == lastYMove && pos.z == lastZMove;
		boolean hasntFallen = lastGround == game.controls.getPlayerGround().toPx().y;
		boolean hasntLooked = rotation == lastRot && tilt == lastTilt;
		if (hasntPressed && hasntMoved && hasntFallen && hasntLooked) {
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
				double ground = game.controls.getPlayerGround().toSubBlock().y;
				double offset = pos.y + ground;
				double skyDepth = (Conversion.blockToPx(World.SKY_Y) - offset) / -vert;
				double worldDepth = (Player.PLAYER_HEIGHT_PX + offset) / vert;
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
				int texelY = (int) game.controls.getPlayerGround().toTx().y;
				int texelZ = (int) (depth * rotCos - horiz * rotSin + pos.z);

				// World block coords
				BlockCoord blockCoord = Coord.fromTx(texelX, texelY, texelZ).toBlock();
				int blockX = blockCoord.x;
				int blockY = blockCoord.y;
				int blockZ = blockCoord.z;

				// Set looking at block
				if (pixelI == width * height / 2) {
					game.player.setLookingAt(blockX, blockY, blockZ);
				}

				// Get texture for block at this coordinate if within render distance
				Render texture = Block.SKY.getTexture();
				boolean withinRenderDist = depth < renderDistPx;
				if (withinRenderDist && !generateSky) {
					// Render block
					texture = game.world.getTextureAt(blockX, blockY, blockZ);
				}
				// Apply texture
				this.pixels[pixelI] = Texture.getTexel(texture, texelX, texelZ);
			}
		}

		// Apply render distance limiter
		this.renderDistLimiter(renderDistPx);

		// Set last control moves
		this.lastXMove = pos.x;
		this.lastYMove = pos.y;
		this.lastZMove = pos.z;
		this.lastGround = game.controls.getPlayerGround().toPx().y;
		this.lastRot = rotation;
		this.lastTilt = tilt;
		this.lastKbdInput = Arrays.copyOf(game.kbdInput, game.kbdInput.length);

	}

	/** Adds depth-based fog to the pixels */
	private void renderDistLimiter(double renderDistance) {
		if (this.fogAlrApplied)
			return;

		for (int i = 0; i < this.width * this.height; i++) {
			// Get pixel colour
			int colour = this.pixels[i];
			// Determine brightness based on depth value from Z buffer
			int brightness = (int) (renderDistance * Options.gamma * 100 / this.depthStore[i]);

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
			super.pixels[i] = r << 16 | g << 8 | b;
			this.fogAlrApplied = true;
		}
	}
}
