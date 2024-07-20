package com.nixinova.graphics;

import com.nixinova.coords.BlockCoord;
import com.nixinova.coords.Coord3;
import com.nixinova.coords.PxCoord;
import com.nixinova.coords.TxCoord;
import com.nixinova.main.Game;

public class BlocksRenderer extends Render {
	private Game game;

	public BlocksRenderer(Render render, Game game) {
		super(render.width, render.height);
		this.game = game;
		super.clearImage();
	}

	public void renderWorld(Game game) {
		// Loop from bottom to top of world
		BlockCoord min = this.game.world.minCorner;
		BlockCoord max = this.game.world.maxCorner;
		for (int x = min.x; x <= max.x; x++) {
			for (int y = min.y; y <= max.y; y++) {
				for (int z = min.z; z <= max.z; z++) {
					// Ensure block is not air
					if (this.game.world.isAir(x, y, z))
						continue;

					// Render block if visible
					boolean isVisible = Raycast.isBlockVisibleToPlayer(this.game, x, y, z);
					if (isVisible) {
						this.renderOneBlock(x, y, z);
					}
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
					// Only render outside faces of the block;
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
		double rot = this.game.controls.getMouseHorizRads();
		double tilt = this.game.controls.getMouseVertRads();

		// Relative position of block in world and player pos
		PxCoord relPos = new PxCoord(txX - camPos.x, txY - camPos.y, txZ - camPos.z);
		double absDistance = Math.sqrt(relPos.x * relPos.x + relPos.y * relPos.y + relPos.z * relPos.z);

		// Apply Y-axis (horiz) rotation
		double xRot = relPos.x * Math.cos(rot) - relPos.z * Math.sin(rot);
		double yRot = relPos.y;
		double zRot = relPos.x * Math.sin(rot) + relPos.z * Math.cos(rot);

		// Apply X-axis (vertical) tilt
		double xTilt = xRot;
		double yTilt = yRot * Math.cos(tilt) - zRot * Math.sin(tilt);
		double zTilt = yRot * Math.sin(tilt) + zRot * Math.cos(tilt);

		// Project to 2D screen space
		double screenX = (this.width / 2.0) + (xTilt / zTilt) * this.height;
		double screenY = (this.height / 2.0) - (yTilt / zTilt) * this.height;

		if (zTilt <= 0 || !isValidPosition((int) screenX, (int) screenY)) {
			// off screen, so return nothing
			return null;
		}

		return new PxCoord(screenX, screenY, absDistance);
	}

	private void generateRenderedTexel(int pixel, PxCoord screenPos, BlockCoord blockPos) {
		final int TEXEL_SIZE = 16;

		int startX = (int) screenPos.x;
		int startY = (int) screenPos.y;

		for (int x = 0; x < TEXEL_SIZE; x++) {
			for (int y = 0; y < TEXEL_SIZE; y++) {
				int screenX = startX + x - Texture.SIZE;
				int screenY = startY + y - Texture.SIZE;

				// Ensure pixel is within screen bounds
				if (isValidPosition(screenX, screenY)) {
					setPixel(screenX, screenY, pixel);
				}
			}
		}
	}

}
