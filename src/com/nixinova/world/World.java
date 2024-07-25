package com.nixinova.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.nixinova.blocks.Block;
import com.nixinova.blocks.BlockFace;
import com.nixinova.coords.BlockCoord;
import com.nixinova.graphics.Render;
import com.nixinova.options.Options;

public class World {
	public static final int SKY_Y = 18;
	public static final int GROUND_Y = 10;

	public final BlockCoord minCorner;
	public final BlockCoord maxCorner;

	private Render[][][] blockTextures;

	public World() {
		this.minCorner = new BlockCoord(0, 0, 0);
		this.maxCorner = new BlockCoord(Options.worldSize, SKY_Y, Options.worldSize);

		this.mapBlockTextures();
	}

	public boolean isWithinWorld(int blockX, int blockY, int blockZ) {
		final BlockCoord min = minCorner, max = maxCorner;
		boolean xValid = blockX >= min.x && blockX <= max.x;
		boolean yValid = blockY >= min.y && blockY <= max.y;
		boolean zValid = blockZ >= min.z && blockZ <= max.z;
		return xValid && yValid && zValid;
	}

	/** returns -1 if all is air */
	public int getMinGroundY(int blockX, int blockZ) {
		for (int i = 0; i < SKY_Y; i++) {
			if (this.getTextureAt(blockX, i, blockZ) == null) {
				return i - 1;
			}
		}
		return -1;
	}

	public boolean isExposed(int blockX, int blockY, int blockZ) {
		int x = blockX, y = blockY, z = blockZ;
		boolean touchingAir = isAir(x, y + 1, z) || isAir(x, y - 1, z)
			|| isAir(x + 1, y, z) || isAir(x - 1, y, z) || isAir(x, y, z + 1) || isAir(x, y, z - 1);
		return touchingAir;
	}

	public boolean isFaceExposed(BlockFace face, int blockX, int blockY, int blockZ) {
		// Get adjacent blocks to check
		List<int[]> offsetsToCheck = new ArrayList<>();
		if (face.xMax)
			offsetsToCheck.add(new int[] { +1, 0, 0 });
		if (face.xMin)
			offsetsToCheck.add(new int[] { -1, 0, 0 });
		if (face.yMax)
			offsetsToCheck.add(new int[] { 0, +1, 0 });
		if (face.yMin)
			offsetsToCheck.add(new int[] { 0, -1, 0 });
		if (face.zMax)
			offsetsToCheck.add(new int[] { 0, 0, +1 });
		if (face.zMin)
			offsetsToCheck.add(new int[] { 0, 0, -1 });

		// Check adjacent blocks and return true if any are air
		for (int[] coords : offsetsToCheck) {
			int x = blockX + coords[0], y = blockY + coords[1], z = blockZ + coords[2];
			boolean isExposed = isWithinWorld(x, y, z) && isAir(x, y, z);
			if (isExposed)
				return true;
		}
		return false;
	}

	public boolean isAir(int blockX, int blockY, int blockZ) {
		return getTextureAt(blockX, blockY, blockZ) == Block.AIR.getTexture();
	}

	public boolean isAir(BlockCoord block) {
		return isAir(block.x, block.y, block.z);
	}

	public Render getTextureAt(int blockX, int blockY, int blockZ) {
		if (isWithinWorld(blockX, blockY, blockZ)) {
			// If within the world, return texture
			return this.blockTextures[blockX][blockY][blockZ];
		} else {
			// When outside of world, return sky
			return Block.SKY.getTexture();
		}
	}

	public Render getTextureAt(BlockCoord block) {
		return getTextureAt(block.x, block.y, block.z);
	}

	public void setTextureAt(BlockCoord block, Render texture) {
		if (isWithinWorld(block.x, block.y, block.z)) {
			this.blockTextures[block.x][block.y][block.z] = texture;
		}
	}

	// NOTE: does not abide by this.minCorner
	private void mapBlockTextures() {
		int maxX = this.maxCorner.x + 1;
		int maxY = this.maxCorner.y + 1;
		int maxZ = this.maxCorner.z + 1;
		this.blockTextures = new Render[maxX][maxY][maxZ];

		Random random = new Random(Options.seed);

		for (int x = 0; x < this.maxCorner.x; x++) {
			for (int z = 0; z < this.maxCorner.z; z++) {
				int localGroundY = GROUND_Y + random.nextInt(2);

				for (int y = 0; y < this.maxCorner.y; y++) {
					Block block;

					if (y == 0)
						block = Block.BEDROCK;
					else if (y <= localGroundY - 4)
						block = Block.STONE;
					else if (y <= localGroundY - 1)
						block = Block.DIRT;
					else if (y <= localGroundY)
						block = Block.GRASS;
					else
						block = Block.AIR;

					blockTextures[x][y][z] = block.getTexture();
				}
			}
		}
	}

}
