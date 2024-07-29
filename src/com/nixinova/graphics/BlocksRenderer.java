package com.nixinova.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

import com.nixinova.PixelColor;
import com.nixinova.Vector3;
import com.nixinova.blocks.BlockFace;
import com.nixinova.coords.BlockCoord;
import com.nixinova.coords.Coord3;
import com.nixinova.coords.PxCoord;
import com.nixinova.coords.TxCoord;
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
		final int size = Texture.SIZE;

		// Loop through texels and render
		TxCoord startTx = Coord3.fromBlock(blockX, blockY, blockZ).toTx();
		for (int txX = 0; txX < size; txX++) {
			for (int txY = 0; txY < size; txY++) {
				for (int txZ = 0; txZ < size; txZ++) {
					// Only render outside faces of the block
					boolean atStart = txX == 0 || txY == 0 || txZ == 0;
					boolean atEnd = txX == size - 1 || txY == size - 1 || txZ == size - 1;
					if (!atStart && !atEnd)
						continue;

					// Get block face
					var blockFaces = BlockFace.getFacesFromTx(txX, txY, txZ);

					// Ignore if this block face is not exposed
					for (BlockFace face : blockFaces) {
						if (!this.game.world.isFaceExposed(face, blockX, blockY, blockZ)) {
							continue;
						}

						// Render this texel
						int x = startTx.x + txX;
						int y = startTx.y + txY;
						int z = startTx.z + txZ;
						this.renderOneTx(face, x, y, z);
					}
				}
			}
		}
	}

	private void renderOneTx(BlockFace face, int txX, int txY, int txZ) {
		TxCoord txCoord = new TxCoord(txX, txY, txZ);
		BlockCoord blockCoord = Coord3.fromTx(txCoord).toBlock();

		// Get texture
		Render texture = this.game.world.getTextureAt(blockCoord);

		// Skip if block is air
		if (texture == null)
			return;

		// Get screen pixel position
		TxCoord tx2 = txCoord;
		TxCoord tx3 = txCoord;
		TxCoord tx4 = txCoord;
		switch (face) {
			case YMIN, YMAX:
				tx2 = new TxCoord(txX, txY, txZ + 1);
				tx3 = new TxCoord(txX + 1, txY, txZ);
				tx3 = new TxCoord(txX + 1, txY, txZ + 1);
				break;
			case XMIN, XMAX:
				tx2 = new TxCoord(txX, txY, txZ + 1);
				tx3 = new TxCoord(txX, txY + 1, txZ);
				tx4 = new TxCoord(txX, txY + 1, txZ + 1);
				break;
			case ZMIN, ZMAX:
				tx2 = new TxCoord(txX, txY + 1, txZ);
				tx3 = new TxCoord(txX + 1, txY, txZ);
				tx4 = new TxCoord(txX + 1, txY + 1, txZ);
				break;
		}
		PxCoord posOnScreen1 = txCoordToScreenPx(txCoord, face.getOffset());
		PxCoord posOnScreen2 = txCoordToScreenPx(tx2, face.getOffset());
		PxCoord posOnScreen3 = txCoordToScreenPx(tx3, face.getOffset());
		PxCoord posOnScreen4 = txCoordToScreenPx(tx4, face.getOffset());

		if (posOnScreen1 == null || posOnScreen2 == null || posOnScreen3 == null || posOnScreen4 == null)
			return;

		// Get texel colour
		int textureX = 0;
		int textureY = 0;
		boolean flipX = false;
		boolean flipY = false;
		switch (face) {
			case XMAX, XMIN -> {
				textureX = txY;
				textureY = txZ;
				flipX = true;
				flipY = face == BlockFace.XMIN;

			}
			case YMAX, YMIN -> {
				textureX = txX;
				textureY = txZ;
				flipX = false;
				flipY = face == BlockFace.YMAX;
			}
			case ZMAX, ZMIN -> {
				textureX = txX;
				textureY = txY;
				flipX = face == BlockFace.ZMAX;
				flipY = true;
			}
		}
		int txPixel = Texture.getTexel(texture, textureX, textureY, flipX, flipY);
		PxCoord[] screenCoords = new PxCoord[] { posOnScreen1, posOnScreen2, posOnScreen3, posOnScreen4 };
		this.saveRect(screenCoords, txPixel, posOnScreen1.z);
	}

	private boolean isWithinRenderDistance(int blockX, int blockY, int blockZ) {
		BlockCoord playerPos = this.game.controls.getFootPosition().toBlock();
		int dx = playerPos.x - blockX;
		int dy = playerPos.y - blockY;
		int dz = playerPos.z - blockZ;
		int distance = (int) Math.sqrt(dx * dx + dy * dy + dz * dz);
		return distance < Options.renderDistance;
	}

	private PxCoord txCoordToScreenPx(TxCoord tx, Vector3<Integer> offset) {
		PxCoord camPos = this.game.controls.getCameraPosition().toPx();

		// Relative position of block in world and player pos
		double relX = tx.x - camPos.x + offset.x * 0.1;
		double relY = tx.y - camPos.y + offset.y * 0.1;
		double relZ = tx.z - camPos.z + offset.z * 0.1;
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

		// Early return when pixel is invalid
		if (zTilt <= 0)
			return null;

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
	private void saveRect(PxCoord[] screenCoords, int pixel, double zIndex) {
		// Construct polygon to draw texel
		var xpoints = new int[screenCoords.length];
		var ypoints = new int[screenCoords.length];
		var pixelIs = new int[screenCoords.length];
		for (short i = 0; i < screenCoords.length; i++) {
			int screenX = (int) screenCoords[i].x;
			int screenY = (int) screenCoords[i].y;

			if (!super.isValidPosition(screenX, screenY))
				return;

			// Don't draw this rectangle if a closer texel has already been drawn
			int pixelI = super.getPixelIndex(screenX, screenY);
			pixelIs[i] = pixelI;
			if (this.pixelCurMinDistances[pixelI] < (int) zIndex)
				return;

			// Add point to polygon
			xpoints[i] = screenX;
			ypoints[i] = screenY;
		}

		// Update min distances
		for (short i = 0; i < screenCoords.length; i++) {
			this.pixelCurMinDistances[pixelIs[i]] = (int) zIndex;
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
