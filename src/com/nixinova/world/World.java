package com.nixinova.world;

import java.util.Random;

import com.nixinova.coords.BlockCoord;
import com.nixinova.graphics.Render;
import com.nixinova.options.Options;

public class World {
	public static final int SKY_Y = 18;
	public static final int GROUND_Y = 10;

	public final BlockCoord minCorner;
	public final BlockCoord maxCorner;

	private final int[] arrSize;

	private Render[][][] blockTextures;

	public World() {
		this.minCorner = new BlockCoord(-Options.worldSize, 0, -Options.worldSize);
		this.maxCorner = new BlockCoord(Options.worldSize, SKY_Y, Options.worldSize);
		this.arrSize = new int[] {
			// to go from +size to -size incl. 0
			maxCorner.x - minCorner.x + 1,
			maxCorner.y - minCorner.y + 1,
			maxCorner.z - minCorner.z + 1,
		};

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

	public boolean isAir(int blockX, int blockY, int blockZ) {
		return getTextureAt(blockX, blockY, blockZ) == Block.AIR.getTexture();
	}

	public Render getTextureAt(int blockX, int blockY, int blockZ) {
		if (isWithinWorld(blockX, blockY, blockZ)) {
			// If within the world, return texture
			BlockCoord coordI = toBlockIndex(blockX, blockY, blockZ);
			return this.blockTextures[coordI.x][coordI.y][coordI.z];
		} else {
			// When outside of world, return sky
			return Block.SKY.getTexture();
		}
	}

	public void setTextureAt(int blockX, int blockY, int blockZ, Render texture) {
		if (isWithinWorld(blockX, blockY, blockZ)) {
			BlockCoord coordI = toBlockIndex(blockX, blockY, blockZ);
			this.blockTextures[coordI.x][coordI.y][coordI.z] = texture;
		}
	}

	private void mapBlockTextures() {
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
					else if ((y * 2 < x + z) && y <= GROUND_Y - 4)
						block = Block.STONE;
					else if ((y * 2 < x + z) && y <= GROUND_Y - 1)
						block = Block.DIRT;
					else if ((y * 2 < x + z) && y <= GROUND_Y)
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

	private int toBlockIndex(int blockCoord) {
		return blockCoord + Options.worldSize;
	}

	private BlockCoord toBlockIndex(int blockX, int blockY, int blockZ) {
		// Note: Y cannot be negative, so no blockIndexing
		return new BlockCoord(toBlockIndex(blockX), blockY, toBlockIndex(blockZ));
	}

}
