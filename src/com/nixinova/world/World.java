package com.nixinova.world;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import com.nixinova.blocks.Block;
import com.nixinova.blocks.BlockFace;
import com.nixinova.coords.BlockCoord;
import com.nixinova.coords.Coord3;
import com.nixinova.graphics.Render;
import com.nixinova.options.Options;

public class World {
	/** Inclusive (>=) */
	public final BlockCoord minCorner;
	/** Exclusive (<) */
	public final BlockCoord maxCorner;

	private Render[][][] blockTextures;
	private Map<BlockCoord, Block> blockChanges;

	public World() {
		this.minCorner = new BlockCoord(0, 0, 0);
		this.maxCorner = new BlockCoord(Options.worldSize, Options.buildHeight, Options.worldSize);
		this.blockChanges = new HashMap<>();

		this.mapBlockTextures();
	}

	public World(Map<BlockCoord, Block> blockChanges) {
		this();
		this.blockChanges = blockChanges;
		// Update world to match block changes map
		for (Entry<BlockCoord, Block> entry : blockChanges.entrySet()) {
			BlockCoord blockPos = entry.getKey();
			Block block = entry.getValue();
			setTextureAt(blockPos, block.getTexture());
		}
	}

	public Map<BlockCoord, Block> getBlockChanges() {
		return blockChanges;
	}

	public boolean isWithinWorld(int blockX, int blockY, int blockZ) {
		final BlockCoord min = minCorner, max = maxCorner;
		boolean xValid = blockX >= min.x && blockX < max.x;
		boolean yValid = blockY >= min.y && blockY < max.y;
		boolean zValid = blockZ >= min.z && blockZ < max.z;
		return xValid && yValid && zValid;
	}

	public boolean isWithinWorld(BlockCoord block) {
		return isWithinWorld(block.x, block.y, block.z);
	}

	/** returns -1 if all is air */
	public int getMinGroundY(int blockX, int blockZ) {
		for (int i = 0; i < Options.buildHeight; i++) {
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
		var offset = face.getOffset();
		int x = blockX + offset.x;
		int y = blockY + offset.y;
		int z = blockZ + offset.z;
		return isAir(x, y, z);
	}

	public boolean isAir(int blockX, int blockY, int blockZ) {
		boolean isOutsideWorld = !isWithinWorld(blockX, blockY, blockZ);
		boolean isAir = getTextureAt(blockX, blockY, blockZ) == Block.AIR.getTexture();
		return isOutsideWorld || isAir;
	}

	public boolean isAir(BlockCoord block) {
		return isAir(block.x, block.y, block.z);
	}

	public Render getTextureAt(int blockX, int blockY, int blockZ) {
		if (isWithinWorld(blockX, blockY, blockZ)) {
			// If within the world, return texture
			return this.blockTextures[blockX][blockY][blockZ];
		} else {
			// Return nothing when outside of world
			return null;
		}
	}

	public Render getTextureAt(BlockCoord block) {
		return getTextureAt(block.x, block.y, block.z);
	}

	private void setTextureAt(BlockCoord block, Render texture) {
		if (isWithinWorld(block.x, block.y, block.z)) {
			this.blockTextures[block.x][block.y][block.z] = texture;
		}
	}

	public void mineBlock(BlockCoord pos) {
		placeBlock(pos, Block.AIR);
	}

	public void placeBlock(BlockCoord pos, Block block) {
		setTextureAt(pos, block.getTexture());
		blockChanges.put(pos, block);
	}
	
	public Coord3 getHorizontalCentre() {
		double x = (this.maxCorner.x + this.minCorner.x) / 2 + 0.5; // centre of block in centre of world
		double y = Options.groundLevel + 1; // one block above the ground
		double z = (this.maxCorner.z + this.minCorner.z) / 2 + 0.5; // centre of block in centre of world
		return Coord3.fromSubBlock(x, y, z);
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
				int localGroundY = Options.groundLevel;

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
