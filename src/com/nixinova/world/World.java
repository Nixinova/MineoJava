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
	public int getGroundY(int blockX, int blockZ) {
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

	public Render getTextureAt(int blockX, int blockY, int blockZ) {
		if (isWithinWorld(blockX, blockY, blockZ)) {
			// If within the world, return texture
			return this.blockTextures[blockX][blockY][blockZ];
		} else {
			// When outside of world, return sky
			return Block.SKY.getTexture();
		}
	}

	public void setTextureAt(int blockX, int blockY, int blockZ, Render texture) {
		if (isWithinWorld(blockX, blockY, blockZ)) {
			this.blockTextures[blockX][blockY][blockZ] = texture;
		}
	}

	// NOTE: does not abide by this.minCorner
	private void mapBlockTextures() {
		int arrSize[] = new int[] { this.maxCorner.x+1, this.maxCorner.y+1, this.maxCorner.z+1 };
		this.blockTextures = new Render[arrSize[0]][arrSize[1]][arrSize[2]];

		Random random = new Random(Options.seed);

		for (int x = 0; x < arrSize[0]; x++) {
			for (int y = 0; y < arrSize[1]; y++) {
				for (int z = 0; z < arrSize[2]; z++) {

					if (y < 0)
						continue;

					Block block;
					if (y == 0)
						block = Block.BEDROCK;
					else if ((y * 2.5 < x + z) && y <= GROUND_Y - 4)
						block = Block.STONE;
					else if ((y * 2.5 < x + z) && y <= GROUND_Y - 1)
						block = Block.DIRT;
					else if ((y * 2.5 < x + z) && y <= GROUND_Y)
						block = Block.GRASS;
					else if ((y * 1.5 > x + z) && y <= GROUND_Y - 1)
						block = Block.DIRT;
					else if ((y * 1.5 > x + z) && y <= GROUND_Y)
						block = Block.GRASS;
					/*
					else if (y <= GROUND_Y - 4)
						block = random.nextInt(y) > 1 ? Block.DIRT : Block.STONE;
					else if (y <= GROUND_Y - 2)
						block = Block.DIRT;
					else if (y <= GROUND_Y)
						block = Block.GRASS;
					else if (y <= GROUND_Y + 1 && random.nextBoolean())
						block = Block.GRASS;
					//*/
					else
						block = Block.AIR;

					blockTextures[x][y][z] = block.getTexture();
				}
			}
		}
	}

}
