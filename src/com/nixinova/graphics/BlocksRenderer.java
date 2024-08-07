package com.nixinova.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

import com.nixinova.PixelColor;
import com.nixinova.blocks.BlockCorners;
import com.nixinova.blocks.BlockFace;
import com.nixinova.blocks.TexelCorners;
import com.nixinova.coords.BlockCoord;
import com.nixinova.coords.PxCoord;
import com.nixinova.coords.SubBlockCoord;
import com.nixinova.main.Game;
import com.nixinova.options.Options;

public class BlocksRenderer extends Render {
	private Game game;
	private Graphics graphics;
	private float[] pixelCurMinDistances;
	private double xRot, yRot;
	private double xRotSin, yRotSin;
	private double xRotCos, yRotCos;

	public BlocksRenderer(int width, int height) {
		super(width, height);
		this.pixelCurMinDistances = new float[width * height];
	}

	public void prepare(Game game, Graphics graphics) {
		this.game = game;
		this.graphics = graphics;

		// Clear graphics
		this.graphics.setColor(Color.black);
		this.graphics.fillRect(0, 0, Display.WIDTH, Display.HEIGHT);

		// Clear image
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
		final BlockFace[] faces = {
			BlockFace.XMIN, BlockFace.XMAX,
			BlockFace.YMIN, BlockFace.YMAX,
			BlockFace.ZMIN, BlockFace.ZMAX,
		};
		for (BlockFace face : faces) {
			renderBlockFace(face, blockX, blockY, blockZ);
		}
	}

	private void renderBlockFace(BlockFace face, int blockX, int blockY, int blockZ) {
		// Exit if face is not exposed
		if (!this.game.world.isFaceExposed(face, blockX, blockY, blockZ)) {
			return;
		}

		Render texture = this.game.world.getTextureAt(blockX, blockY, blockZ);

		// Get corners of block
		BlockCorners blockCorners = new BlockCorners(blockX, blockY, blockZ, face);
		TexelCorners texCornersList = new TexelCorners(blockCorners);

		// Get corners of each texel and render
		for (int texX = 0; texX < Texture.SIZE; texX++) {
			for (int texY = 0; texY < Texture.SIZE; texY++) {
				SubBlockCoord[] curTexCorners = texCornersList.getTexelCorners(texX, texY);

				// Get screen coords for each corner
				PxCoord[] polygonCorners = new PxCoord[curTexCorners.length];
				for (int i = 0; i < polygonCorners.length; i++) {
					polygonCorners[i] = coordToScreenPx(curTexCorners[i]);
				}

				// Save texel
				int txPixel = Texture.getTexel(texture, texX, texY);
				saveRect(polygonCorners, txPixel);
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

	private PxCoord coordToScreenPx(SubBlockCoord block) {
		var camPos = this.game.controls.getCameraPosition().toSubBlock();

		// Relative position of block in world and player pos
		double relX = block.x - camPos.x;
		double relY = block.y - camPos.y;
		double relZ = block.z - camPos.z;
		double absDistance = Math.sqrt(relX * relX + relY * relY + relZ * relZ);

		// Apply Y-axis (horiz) rotation
		double xRot = relX * this.xRotCos - relZ * this.xRotSin;
		double yRot = relY;
		double zRot = relX * this.xRotSin + relZ * this.xRotCos;

		// Apply X-axis (vertical) tilt
		double xTilt = xRot;
		double yTilt = yRot * this.yRotCos - zRot * this.yRotSin;
		double zTilt = yRot * this.yRotSin + zRot * this.yRotCos;

		// Project to 2D screen space
		double screenX = (this.width / 2.0) + (xTilt / zTilt) * this.height;
		double screenY = (this.height / 2.0) - (yTilt / zTilt) * this.height;

		return new PxCoord(screenX, screenY, absDistance);
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
	private void saveRect(PxCoord[] screenCoords, int pixel) {
		// Construct polygon to draw texel
		var xpoints = new int[screenCoords.length];
		var ypoints = new int[screenCoords.length];
		var pixelIs = new int[screenCoords.length];
		int zIndex = 0;
		for (short i = 0; i < screenCoords.length; i++) {
			int screenX = (int) screenCoords[i].x;
			int screenY = (int) screenCoords[i].y;

			if (!super.isValidPosition(screenX, screenY))
				return;

			// Average z-index
			zIndex += (int) screenCoords[i].z;
			zIndex /= (i + 1);

			// Don't draw this rectangle if a closer texel has already been drawn
			final int zIndexPadding = 10; // margin of error
			int pixelI = super.getPixelIndex(screenX, screenY);
			pixelIs[i] = pixelI;
			if (this.pixelCurMinDistances[pixelI] < zIndex - zIndexPadding)
				return;

			// Update min distance
			this.pixelCurMinDistances[pixelIs[i]] = zIndex;

			// Add point to polygon
			xpoints[i] = screenX;
			ypoints[i] = screenY;
		}

		// Apply fog to pixel
		double brightAmount = Options.gamma * 10 * (Options.renderDistance - zIndex / 10);
		int fogAppliedPixel = applyFog(pixel, (int) brightAmount);

		// Draw pixel
		Polygon polygon = new Polygon(xpoints, ypoints, xpoints.length);
		this.graphics.setColor(PixelColor.fromPixel(fogAppliedPixel));
		this.graphics.fillPolygon(polygon);
	}

}
