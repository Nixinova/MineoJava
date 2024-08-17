package com.nixinova.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private static final double SKY_SIZE = 1.5;

	private Game game;
	private Graphics graphics;
	private Map<Integer, List<SavedPolygon>> savedPolygons;
	private double xRot, yRot;
	private double xRotSin, yRotSin;
	private double xRotCos, yRotCos;
	private BlockCoord lookingAt;

	public BlocksRenderer(int width, int height) {
		super(width, height);
	}

	public void prepare(Game game, Graphics graphics) {
		this.game = game;
		this.graphics = graphics;

		// Clear graphics
		this.graphics.setColor(Color.black);
		this.graphics.fillRect(0, 0, Display.WIDTH, Display.HEIGHT);

		// Clear image
		savedPolygons = new HashMap<>();

		// Cache trig
		this.xRot = this.game.controls.getMouseHorizRads();
		this.yRot = this.game.controls.getMouseVertRads();
		this.xRotSin = Math.sin(this.xRot);
		this.xRotCos = Math.cos(this.xRot);
		this.yRotSin = Math.sin(this.yRot);
		this.yRotCos = Math.cos(this.yRot);

		// Save looking at block
		this.lookingAt = Raycast.getLookingAt(game).hoveredBlock;
	}

	public void renderWorld() {
		// Generate world drawing
		drawSky();
		drawWorld();

		// Render world from far to near, so closer blocks render over farther blocks
		var zIndexesSet = this.savedPolygons.keySet();
		var zIndexesList = new ArrayList<>(zIndexesSet);
		Collections.sort(zIndexesList, Collections.reverseOrder()); // sort far to near
		for (int zIndex : zIndexesList) {
			// Render all polygons
			for (SavedPolygon savedPolygon : this.savedPolygons.get(zIndex)) {
				drawPolygon(savedPolygon);
			}
		}
	}

	private void drawSky() {
		final int baseBlue = 0x0000AA;
		final int bandHeight = 10;
		final double gradientMult = 0.97;

		double tilt = this.game.controls.getMouseVertRads();
		double skySize = Display.HEIGHT * (SKY_SIZE + tilt) + this.game.player.getPosition().toBlock().y;

		// Generate starting blue colour from mouse angle
		int currentBlue = baseBlue;
		for (double i = tilt; i > 0; i -= 0.01) {
			// brighten blue as player is looking upwards
			currentBlue /= gradientMult;
		}
		for (double i = tilt; i < 0; i += 0.01) {
			// darken blue as player is looking downwards
			currentBlue *= gradientMult;
		}
		// create bands of sky colour
		for (int vert = 0; vert < skySize; vert += bandHeight) {
			currentBlue *= gradientMult;
			int gradientedBlue = currentBlue;
			if (gradientedBlue > 0xFF)
				gradientedBlue = 0xFF;
			if (gradientedBlue < 0)
				gradientedBlue = 0;

			this.graphics.setColor(PixelColor.fromPixel(gradientedBlue));
			this.graphics.fillRect(0, vert, Display.WIDTH, vert + bandHeight);
		}
	}

	private void drawWorld() {
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

					// Early return if block is exposed to air
					if (!game.world.isExposed(x, y, z))
						continue;

					// Ensure block is visible to player
					boolean isInSight = Raycast.isBlockVisibleToPlayer(this.game, x, y, z);
					if (!isInSight)
						continue;

					// Render block
					drawOneBlock(x, y, z);
				}
			}
		}
	}

	private void drawOneBlock(int blockX, int blockY, int blockZ) {
		Render texture = this.game.world.getTextureAt(blockX, blockY, blockZ);

		final BlockFace[] faces = {
			BlockFace.XMIN, BlockFace.XMAX,
			BlockFace.YMIN, BlockFace.YMAX,
			BlockFace.ZMIN, BlockFace.ZMAX,
		};
		for (BlockFace face : faces) {
			drawBlockFace(face, blockX, blockY, blockZ, texture);
		}
	}

	private void drawBlockFace(BlockFace face, int blockX, int blockY, int blockZ, Render texture) {
		// Exit if face is not exposed
		if (!this.game.world.isFaceExposed(face, blockX, blockY, blockZ)) {
			return;
		}

		// Get corners of block
		BlockCorners blockCorners = new BlockCorners(blockX, blockY, blockZ, face);
		TexelCorners texCornersList = new TexelCorners(blockCorners);

		// Get corners of each texel and render
		for (int texX = 0; texX < Texture.SIZE; texX++) {
			yloop: for (int texY = 0; texY < Texture.SIZE; texY++) {
				SubBlockCoord[] curTexCorners = texCornersList.getTexelCorners(texX, texY);

				// Get screen coords for each corner
				PxCoord[] polygonCorners = new PxCoord[curTexCorners.length];
				for (int i = 0; i < polygonCorners.length; i++) {
					PxCoord screenPos = coordToScreenPx(curTexCorners[i], false);

					// Kill polygon if one corner position is not valid
					if (screenPos == null) {
						continue yloop;
					}

					polygonCorners[i] = screenPos;
				}

				// Get texel
				int txPixel = Texture.getTexel(texture, texX, texY);

				// White border for hovered block
				boolean isLookingAt = this.lookingAt != null
					&& this.lookingAt.x == blockX && this.lookingAt.y == blockY && this.lookingAt.z == blockZ;
				boolean onBorder = texX == 0 || texX == Texture.SIZE - 1 || texY == 0 || texY == Texture.SIZE - 1;
				if (isLookingAt && onBorder) {
					txPixel = PixelColor.SELECTION_OUTLINE;
				}

				// Save texel
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

	private PxCoord coordToScreenPx(SubBlockCoord block, boolean force) {
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

		// Early return when pixel is invalid
		if (zTilt < 0 && !force)
			return null;

		// Project to 2D screen space
		double screenX = (this.width / 2.0) + (xTilt / zTilt) * this.height;
		double screenY = (this.height / 2.0) - (yTilt / zTilt) * this.height;

		return new PxCoord(screenX, screenY, absDistance);
	}

	/** Adds depth-based fog to the pixels */
	private int applyFog(int colour, double distance) {
		double base = Options.gamma * (Options.renderDistance - distance);
		short brightness = (short) (base * base);
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
		double zIndex = 0;

		for (short i = 0; i < screenCoords.length; i++) {
			int screenX = (int) screenCoords[i].x;
			int screenY = (int) screenCoords[i].y;

			// Average z-index
			zIndex += screenCoords[i].z;
			zIndex /= 2;

			// Ignore pixels that are too far off-screen
			if (!super.isValidPosition(screenX, screenY)) {
				final int screenPadding = 10; // render only this distance off the side of the screen
				boolean offScreenX = screenX < -screenPadding || screenX > width + screenPadding;
				boolean offScreenY = screenY < -screenPadding || screenY > height + screenPadding;
				if (offScreenX && offScreenY)
					return;
			}

			// Add point to polygon
			xpoints[i] = screenX;
			ypoints[i] = screenY;
		}

		// Apply fog to pixel
		int fogAppliedPixel = applyFog(pixel, zIndex);

		// Save polygon data to list
		var polygon = new Polygon(xpoints, ypoints, xpoints.length);
		var savedPolygon = new SavedPolygon(polygon, fogAppliedPixel);
		int zKey = (int) (zIndex * Texture.SIZE);
		// Initialise array at z index if not present
		if (!this.savedPolygons.containsKey(zKey)) {
			this.savedPolygons.put(zKey, new ArrayList<>());
		}
		this.savedPolygons.get(zKey).add(savedPolygon);
	}

	private void drawPolygon(SavedPolygon savedPolygon) {
		this.graphics.setColor(PixelColor.fromPixel(savedPolygon.pixel));
		this.graphics.fillPolygon(savedPolygon.polygon);
	}

}
