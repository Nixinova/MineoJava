package com.nixinova.graphics;

import com.nixinova.Conversion;
import com.nixinova.coords.BlockCoord;
import com.nixinova.coords.PxCoord;
import com.nixinova.coords.SubBlockCoord;
import com.nixinova.main.Game;
import com.nixinova.player.Player;

public class BlocksRenderer extends Render {

	private Game game;

	public BlocksRenderer(Render render, Game game) {
		super(render.width, render.height);
		this.game = game;
		super.clearImage();
	}

	public void renderWorld() {
		// Loop from bottom to top of world
		BlockCoord min = this.game.world.minCorner;
		BlockCoord max = this.game.world.maxCorner;
		for (int y = min.y; y <= max.y; y++) {
			for (int x = min.x; x <= max.x; x++) {
				for (int z = min.z; z <= max.z; z++) {
					// if (this.game.world.isExposed(x, y, z))
						this.renderOneBlock(x, y, z);
				}
			}
		}
	}

	public void renderOneBlock(int blockX, int blockY, int blockZ) {
		Render texture = this.game.world.getTextureAt(blockX, blockY, blockZ);

		// Skip if air
		if (texture == null) {
			return;
		}

		PxCoord posOnScreen = blockCoordToScreenPx(blockX, blockY, blockZ);
		if (posOnScreen != null) {
			// Render block
			BlockCoord blockPos = new BlockCoord(blockX, blockY, blockZ);
			this.generateRenderedBlock(texture, blockPos, posOnScreen);
		}
	}

	public PxCoord blockCoordToScreenPx(int blockX, int blockY, int blockZ) {
		SubBlockCoord playerPos = this.game.controls.getPosition().toSubBlock();
		playerPos.y += Player.PLAYER_HEIGHT;
		double rot = this.game.controls.getXRot();
		double tilt = this.game.controls.getYRot();

		// Relative position of block in world and player pos
		SubBlockCoord relPos = new SubBlockCoord(blockX - playerPos.x, blockY - playerPos.y, blockZ - playerPos.z);
		double absDistance = Math.sqrt(relPos.x * relPos.x + relPos.y * relPos.y + relPos.z * relPos.z);

		// Apply Y-axis (horiz) rotation
		double xRot = relPos.x * Math.cos(rot) - relPos.z * Math.sin(rot);
		double yRot = relPos.y;// * Math.cos(tilt) - relPos.z * Math.sin(tilt);
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

	public void generateRenderedBlock(Render texture, BlockCoord blockPos, PxCoord screenPos) {
		final int TEX_SIZE = Conversion.PX_PER_BLOCK;
		final int baseBlockSize = 12; // temporary measure

		int startX = (int) screenPos.x;
		int startY = (int) screenPos.y;
		double relDepth = screenPos.z;

		int size = (int) (TEX_SIZE * TEX_SIZE * baseBlockSize / relDepth);

		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				int screenX = startX + x - size / 2;
				int screenY = startY + y - size / 2;

				// Ensure pixel is within screen bounds
				if (isValidPosition(screenX, screenY)) {
					// Set textured screen pixel
					int texelX = x * TEX_SIZE / size;
					int texelY = y * TEX_SIZE / size;
					setPixel(screenX, screenY, Texture.getTexel(texture, texelX, texelY));

					// Save selected block to player if in center of screen
					if (getPixelIndex(screenX, screenY) == imageSize() / 2) {
						setAsSelectedBlock(blockPos);
					}

				}
			}
		}
	}

	private void setAsSelectedBlock(BlockCoord blockPos) {
		this.game.player.setLookingAt(blockPos.x, blockPos.y, blockPos.z);
	}

}
