package com.nixinova.world;

import java.util.Random;

import com.nixinova.graphics.Render;
import com.nixinova.readwrite.Options;
import com.nixinova.types.BlockCoord;
import com.nixinova.types.Conversion;

public class World {
	
	public static final int PLAYER_HEIGHT = 2 * Conversion.PX_PER_BLOCK;
	public static final int SKY_HEIGHT = 18 * Conversion.PX_PER_BLOCK;

	private final int arrSize;
	private final Render[] groundBlocks = new Render[] {
		Block.GRASS.getTexture(),
		Block.DIRT.getTexture(),
		Block.STONE.getTexture(),
	};

	private Render[][][] blockTextures;

	public World() {
		this.arrSize = Options.worldSize * 2 + 1; // to go from +size to -size incl. 0
		this.mapBlockTextures();
	}

	public Render getTextureAt(int blockX, int blockY, int blockZ) {
		BlockCoord coordI = toBlockIndex(new BlockCoord(blockX, blockY, blockZ));

		try {
			// If within the world, return texture
			// Throws when block is outside of world
			return this.blockTextures[coordI.x][coordI.y][coordI.z];
		} catch (ArrayIndexOutOfBoundsException err) {
			// When outside of world, return sky texture
			return Block.SKY.getTexture();
		}
	}

	public void setTextureAt(int blockX, int blockY, int blockZ, Render texture) {
		BlockCoord coordI = toBlockIndex(new BlockCoord(blockX, blockY, blockZ));
		try {
			// Throws when block is outside of world
			this.blockTextures[coordI.x][coordI.y][coordI.z] = texture;
		} catch (ArrayIndexOutOfBoundsException err) {
			// Do nothing if trying to place outside of world
		}
	}

	private void mapBlockTextures() {
		this.blockTextures = new Render[arrSize][arrSize][arrSize];

		Random random = new Random(Options.seed);

		// TODO: 3D coord-based: y=0 place block, otherwise AIR
		for (int x = 0; x < arrSize; x++) {
			for (int y = 0; y < arrSize; y++) {
				for (int z = 0; z < arrSize; z++) {
					BlockCoord coordI = toBlockCoord(new BlockCoord(x, y, z));
					Render texture;

					if (coordI.x == 0 || coordI.z == 0)
						// Bedrock along world origin
						texture = Block.BEDROCK.getTexture();
					else
						texture = this.groundBlocks[random.nextInt(this.groundBlocks.length)];

					blockTextures[x][y][z] = texture;
				}
			}
		}
	}

	private int toBlockIndex(int blockCoord) {
		return blockCoord + Options.worldSize;
	}

	private BlockCoord toBlockIndex(BlockCoord blockCoord) {
		return new BlockCoord(toBlockIndex(blockCoord.x), toBlockIndex(blockCoord.y), toBlockIndex(blockCoord.z));
	}

	private int toBlockCoord(int blockIndex) {
		return blockIndex - Options.worldSize;
	}

	private BlockCoord toBlockCoord(BlockCoord indexCoord) {
		return new BlockCoord(toBlockCoord(indexCoord.x), toBlockCoord(indexCoord.y), toBlockCoord(indexCoord.z));
	}

}
