package com.nixinova.world;

import java.util.Random;

import com.nixinova.coords.BlockCoord;
import com.nixinova.graphics.Render;
import com.nixinova.options.Options;

public class World {
	public static final int SKY_Y = 18;
	public static final int GROUND_Y = 10;

	private final int arrSize;

	private Render[][][] blockTextures;

	public World() {
		this.arrSize = Options.worldSize * 2 + 1; // to go from +size to -size incl. 0
		this.mapBlockTextures();
	}

	public boolean inWorld(int blockX, int blockY, int blockZ) {
		final int wS = Options.worldSize;
		return blockX <= wS && blockX >= -wS && blockY <= SKY_Y && blockY >= 0 && blockZ <= wS && blockZ >= -wS;
	}

	public Render getTextureAt(int blockX, int blockY, int blockZ) {

		BlockCoord coordI = toBlockIndex(blockX, blockY, blockZ);
		if (inWorld(blockX, blockY, blockZ)) {
			// If within the world, return texture
			// Throws when block is outside of world
			return this.blockTextures[coordI.x][coordI.y][coordI.z];
		} else {
			// When outside of world, return sky texture
			return Block.SKY.getTexture();
		}
	}

	public void setTextureAt(int blockX, int blockY, int blockZ, Render texture) {
		BlockCoord coordI = toBlockIndex(blockX, blockY, blockZ);
		if (inWorld(blockX, blockY, blockZ)) {
			// Throws when block is outside of world
			this.blockTextures[coordI.x][coordI.y][coordI.z] = texture;
		} else {
			// Do nothing if trying to place outside of world
		}
	}

	private void mapBlockTextures() {
		this.blockTextures = new Render[arrSize][arrSize][arrSize];

		Random random = new Random(Options.seed);

		for (int x = 0; x < arrSize; x++) {
			for (int y = 0; y < arrSize; y++) {
				for (int z = 0; z < arrSize; z++) {
					int yCoord = fromBlockIndex(y);

					if (yCoord < 0)
						continue;

					Block block;
					if (yCoord <= 0)
						block = Block.BEDROCK;
					else if (yCoord <= GROUND_Y - 4)
						block = random.nextInt(yCoord) > 1 ? Block.DIRT : Block.STONE;
					else if (yCoord <= GROUND_Y - 2)
						block = Block.DIRT;
					else if (yCoord <= GROUND_Y)
						block = Block.GRASS;
					else
						block = null;

					blockTextures[x][y][z] = block == null ? null : block.getTexture();
				}
			}
		}
	}

	private int toBlockIndex(int blockCoord) {
		return blockCoord + Options.worldSize;
	}

	private BlockCoord toBlockIndex(int blockX, int blockY, int blockZ) {
		return new BlockCoord(toBlockIndex(blockX), toBlockIndex(blockY), toBlockIndex(blockZ));
	}

	private int fromBlockIndex(int blockIndex) {
		return blockIndex - Options.worldSize;
	}

}
