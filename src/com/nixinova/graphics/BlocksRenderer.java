package com.nixinova.graphics;

import com.nixinova.coords.BlockCoord;
import com.nixinova.coords.Coord3;
import com.nixinova.coords.PxCoord;
import com.nixinova.coords.TxCoord;
import com.nixinova.main.Game;
import com.nixinova.options.Options;

public class BlocksRenderer extends Render {
	private Game game;

	private float[] pixelCurMinDistances;
	private double xRot, yRot;
	private double xRotSin, yRotSin;
	private double xRotCos, yRotCos;

	public BlocksRenderer(int width, int height) {
		super(width, height);
		this.pixelCurMinDistances = new float[width * height];
	}

	public void prepare(Game game) {
		this.game = game;
		super.clearImage();
		for (int i = 0; i < super.imageSize(); i++) {
			this.pixelCurMinDistances[i] = Integer.MAX_VALUE;
		}

		// Cache trig
		this.xRot = this.game.controls.getMouseHorizRads();
		this.yRot = this.game.controls.getMouseVertRads();
		this.xRotSin = Math.sin(this.xRot);
		this.xRotCos = Math.cos(this.xRot);
		this.yRotSin = Math.sin(this.yRot);
		this.yRotCos = Math.cos(this.yRot);
	}

	public void renderWorld() {
		// Loop from bottom to top of world
		BlockCoord min = this.game.world.minCorner;
		BlockCoord max = this.game.world.maxCorner;
		for (int x = min.x; x <= max.x; x++) {
			for (int y = min.y; y <= max.y; y++) {
				for (int z = min.z; z <= max.z; z++) {
					// Ensure block is not air
					boolean isAir = this.game.world.isAir(x, y, z);
					if (isAir)
						continue;

					// Ensure block is within render distance
					boolean inRenderDist = this.isWithinRenderDistance(x, y, z);
					if (!inRenderDist)
						continue;

					// Ensure block is visible to player
					boolean isInSight = Raycast.isBlockVisibleToPlayer(this.game, x, y, z);
					if (!isInSight)
						continue;

					// Render block
					this.renderOneBlock(x, y, z);
				}
			}
		}
	}

	private void renderOneBlock(int blockX, int blockY, int blockZ) {
		final int size = Texture.SIZE;

		// Loop through texels and render
		// TODO fill inbetween as well
		TxCoord startTx = Coord3.fromBlock(blockX, blockY, blockZ).toTx();
		for (int txX = 0; txX < size; txX++) {
			for (int txY = 0; txY < size; txY++) {
				for (int txZ = 0; txZ < size; txZ++) {
					// Only render outside faces of the block
					boolean atStart = txX == 0 || txY == 0 || txZ == 0;
					boolean atEnd = txX == size - 1 || txY == size - 1 || txZ == size - 1;
					if (!atStart && !atEnd)
						continue;

					// Render this texel
					this.renderOneTx(startTx.x + txX, startTx.y + txY, startTx.z + txZ);
				}
			}
		}
	}

	private void renderOneTx(int txX, int txY, int txZ) {
		// Get texture
		BlockCoord blockCoord = Coord3.fromTx(txX, txY, txZ).toBlock();
		Render texture = this.game.world.getTextureAt(blockCoord.x, blockCoord.y, blockCoord.z);

		// Skip if air
		if (texture == null) {
			return;
		}

		PxCoord posOnScreen = txCoordToScreenPx(txX, txY, txZ);
		if (posOnScreen != null) {
			// Render texel
			int txPixel = Texture.getTexel(texture, txX, txZ);
			this.generateRenderedTexel(txPixel, posOnScreen, blockCoord);
		}
	}

	private PxCoord txCoordToScreenPx(int txX, int txY, int txZ) {
		PxCoord camPos = this.game.controls.getCameraPosition().toPx();

		// Relative position of block in world and player pos
		PxCoord relPos = new PxCoord(txX - camPos.x, txY - camPos.y, txZ - camPos.z);
		double absDistance = Math.sqrt(relPos.x * relPos.x + relPos.y * relPos.y + relPos.z * relPos.z);

		// Apply Y-axis (horiz) rotation
		double xRot = relPos.x * this.xRotCos - relPos.z * this.xRotSin;
		double yRot = relPos.y;
		double zRot = relPos.x * this.xRotSin + relPos.z * this.xRotCos;

		// Apply X-axis (vertical) tilt
		double xTilt = xRot;
		double yTilt = yRot * this.yRotCos - zRot * this.yRotSin;
		double zTilt = yRot * this.yRotSin + zRot * this.yRotCos;

		// Project to 2D screen space
		double screenX = (this.width / 2.0) + (xTilt / zTilt) * this.height;
		double screenY = (this.height / 2.0) - (yTilt / zTilt) * this.height;

		// Early return when pixel is invalid (i.e., offscreen)
		if (zTilt <= 0 || !super.isValidPosition((int) screenX, (int) screenY))
			return null;

		return new PxCoord(screenX, screenY, absDistance);
	}

	private void generateRenderedTexel(int pixel, PxCoord screenPos, BlockCoord blockPos) {
		final int TEXEL_SIZE = 16;

		int startX = (int) screenPos.x;
		int startY = (int) screenPos.y;
		double zIndex = screenPos.z;

		// Apply fog to pixel
		int brightAmount = (int) (Options.gamma * 10 * (Options.renderDistance - zIndex / 10));
		int fogAppliedPixel = applyFog(pixel, brightAmount);

		// Transform texel coordinates based on camera rotation
		for (int i = 0; i < TEXEL_SIZE; i++) {
			for (int j = 0; j < TEXEL_SIZE; j++) {
				// Convert to screen coordinates
				int screenX = startX + i;
				int screenY = startY + j;

				// Check if the position is valid and save the pixel
				if (super.isValidPosition(screenX, screenY)) {
					this.savePixel(screenX, screenY, fogAppliedPixel, zIndex);
				}
			}
		}
	}

	private boolean isWithinRenderDistance(int blockX, int blockY, int blockZ) {
		BlockCoord playerPos = this.game.controls.getFootPosition().toBlock();
		int dx = playerPos.x - blockX;
		int dy = playerPos.y - blockY;
		int dz = playerPos.z - blockZ;
		int distance = (int) Math.sqrt(dx * dx + dy * dy + dz * dz);
		return distance < Options.renderDistance;
	}

	/** Adds depth-based fog to the pixels */
	private int applyFog(int colour, int brightness) {
		// Clamp to 8-bit range
		if (brightness < 0)
			brightness = 0;
		if (brightness > 0xFF)
			brightness = 0xFF;

		// Calculate final RGB from pixel colour + fog
		int r = colour >> 16 & 0xFF;
		int g = colour >> 8 & 0xFF;
		int b = colour & 0xFF;
		r = r * brightness / 0xFF;
		g = g * brightness / 0xFF;
		b = b * brightness / 0xFF;

		// Save fog-adjusted colour to pixel
		return r << 16 | g << 8 | b;
	}

	/** Update a pixel in the current screen image if it is closer to the player than any other pixel at that coordinate */
	private void savePixel(int screenX, int screenY, int pixel, double zIndex) {
		int pixelI = super.getPixelIndex(screenX, screenY);
		double curMinDist = this.pixelCurMinDistances[pixelI];

		// If distance is greater than current min dist, do not render the pixel
		if (zIndex >= curMinDist)
			return;

		// Otherwise save pixel for rendering
		super.setPixel(screenX, screenY, pixel);
		this.pixelCurMinDistances[pixelI] = (float) zIndex;
	}

}
