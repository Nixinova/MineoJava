package com.nixinova.mineo.world;

import java.util.Random;

import com.nixinova.mineo.io.Options;
import com.nixinova.mineo.maths.coords.BlockCoord;
import com.nixinova.mineo.maths.coords.Coord3;
import com.nixinova.mineo.world.blocks.Block;
import com.nixinova.mineo.world.blocks.BlockFace;

public class World {
	/** Inclusive (>=) */
	public static final BlockCoord minCorner = new BlockCoord(0, 0, 0); // Codebase assumes min corner of world is always 0
	/** Exclusive (<) */
	public static final BlockCoord maxCorner = new BlockCoord(Options.worldSize, Options.buildHeight, Options.worldSize);

	private Block[][][] worldBlocks;

	public World() {
		this.mapBlocks();
	}

	public World(Block[][][] blockChanges) {
		this();
		this.worldBlocks = blockChanges;
		// Update world to match block changes map
		for (int x = minCorner.x; x < maxCorner.x; x++) {
			for (int y = minCorner.y; y < maxCorner.y; y++) {
				for (int z = minCorner.z; z < maxCorner.z; z++) {
					Block block = blockChanges[x][y][z];
					if (block == null)
						block = Block.AIR;
					setBlockAt(x, y, z, block);
				}
			}
		}
	}

	public boolean isWithinWorld(int blockX, int blockY, int blockZ) {
		boolean xValid = blockX >= minCorner.x && blockX < maxCorner.x;
		boolean yValid = blockY >= minCorner.y && blockY < maxCorner.y;
		boolean zValid = blockZ >= minCorner.z && blockZ < maxCorner.z;
		return xValid && yValid && zValid;
	}

	public boolean isWithinWorld(BlockCoord block) {
		return isWithinWorld(block.x, block.y, block.z);
	}

	public boolean isWithinWorldBounds(int blockX, int blockY, int blockZ) {
		boolean xValid = blockX >= minCorner.x && blockX < maxCorner.x;
		boolean yValid = blockY >= minCorner.y;
		boolean zValid = blockZ >= minCorner.z && blockZ < maxCorner.z;
		return xValid && yValid && zValid;
	}

	public boolean isWithinWorldBounds(BlockCoord block) {
		return isWithinWorldBounds(block.x, block.y, block.z);
	}

	/** returns -1 if all is air */
	public int getMinGroundY(int blockX, int blockZ) {
		for (int i = 0; i < Options.buildHeight; i++) {
			if (this.getBlockAt(blockX, i, blockZ) == Block.AIR) {
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
		var block = getBlockAt(blockX, blockY, blockZ);
		return block == null || block == Block.AIR;
	}

	public boolean isAir(BlockCoord block) {
		return isAir(block.x, block.y, block.z);
	}

	public Block getBlockAt(int blockX, int blockY, int blockZ) {
		if (isWithinWorld(blockX, blockY, blockZ)) {
			// If within the world, return texture
			return this.worldBlocks[blockX][blockY][blockZ];
		} else {
			// Return nothing when outside of world
			return null;
		}
	}

	public Block getBlockAt(BlockCoord block) {
		return getBlockAt(block.x, block.y, block.z);
	}

	private void setBlockAt(int blockX, int blockY, int blockZ, Block block) {
		if (isWithinWorld(blockX, blockY, blockZ)) {
			this.worldBlocks[blockX][blockY][blockZ] = block;
		}
	}

	private void setBlockAt(BlockCoord pos, Block block) {
		setBlockAt(pos.x, pos.y, pos.z, block);
	}

	public void mineBlock(BlockCoord pos) {
		placeBlock(pos, Block.AIR);
	}

	public void placeBlock(BlockCoord pos, Block block) {
		setBlockAt(pos, block);
	}
	
	public Coord3 getHorizontalCentre() {
		double x = maxCorner.x / 2 + 0.5; // centre of block in centre of world
		double y = Options.buildHeight / 2;
		double z = maxCorner.z / 2 + 0.5; // centre of block in centre of world
		return Coord3.fromSubBlock(x, y, z);
	}

	private void mapBlocks() {
		this.worldBlocks = new Block[maxCorner.x][maxCorner.y][maxCorner.z];
		
		var terrainGrounds = new int[maxCorner.x][maxCorner.z];

		Random random = new Random(Options.seed);

		for (int x = 0; x < maxCorner.x; x++) {
			for (int z = 0; z < maxCorner.z; z++) {
				int localGroundY = Options.groundLevel;
				
				// average ground from surrounding blocks
				if (x > 1 && z > 1)
					localGroundY = (terrainGrounds[x - 1][z - 1] + terrainGrounds[x][z - 1] + terrainGrounds[x - 1][z]) / 3;
				// since the above averaging rounds down with weight 1/3, roughly reverse that effect here to create even terrain
				if (random.nextFloat(1.0f) < 0.35f) localGroundY++;
				// clamp
				localGroundY = Math.max(localGroundY, 4);
				localGroundY = Math.min(localGroundY, Options.buildHeight - 4);
	
				terrainGrounds[x][z] = localGroundY;

				for (int y = 0; y < maxCorner.y; y++) {
					Block block;

					if (y == 0)
						block = Block.BEDROCK;
					else if (y <= localGroundY - 4)
						block = Block.STONE;
					else if (y <= localGroundY - 1)
						block = Block.DIRT;
					else if (y == localGroundY && y >= Options.groundLevel)
						block = Block.GRASS;
					else
						block = Block.AIR;

					worldBlocks[x][y][z] = block;
				}
			}
		}
	}

}
