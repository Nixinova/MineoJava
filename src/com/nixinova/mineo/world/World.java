package com.nixinova.mineo.world;

import java.util.HashMap;
import java.util.Map;
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
	private Map<BlockCoord, Block> blockChanges;

	public World() {
		this.mapBlocks();
		this.blockChanges = new HashMap<BlockCoord, Block>();
	}

	public World(Map<BlockCoord, Block> blockChanges) {
		this();

		// Update world to match block changes map
		for (var blockCoord : blockChanges.keySet()) {
			Block block = blockChanges.get(blockCoord);
			if (block == null)
				block = Block.AIR;
			setBlockAt(blockCoord, block);
		}

		this.blockChanges = blockChanges;
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
			this.blockChanges.put(new BlockCoord(blockX, blockY, blockZ), block);
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
	
	public Map<BlockCoord, Block> getBlockChanges() {
		return this.blockChanges;
	}

	private void mapBlocks() {
		this.worldBlocks = new Block[maxCorner.x][maxCorner.y][maxCorner.z];
		
		var terrainGrounds = new int[maxCorner.x][maxCorner.z];

		Random random = new Random(Options.seed);

		// First round - generate the base terrain
		for (int x = 0; x < maxCorner.x; x++) {
			for (int z = 0; z < maxCorner.z; z++) {
				int localGroundY = Options.groundLevel;

				// average ground from surrounding blocks
				if (x > 1 && z > 1)
					localGroundY = (terrainGrounds[x - 1][z - 1] + terrainGrounds[x][z - 1] + terrainGrounds[x - 1][z]) / 3;
				// since the above averaging rounds down with weight 1/3, roughly reverse that effect here to create even terrain
				if (random.nextFloat(1.0f) < 0.3f) localGroundY++;
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
		
		// Second round - generate trees
		final int distFromEdge = 5; // dont put trees on edge of world
		final int maxTreeHeight = 6;
		final int leafHeight = 3;
		final float treeChance = 0.01f;
		for (int x = distFromEdge; x < maxCorner.x - distFromEdge; x++) {
			for (int z = distFromEdge; z < maxCorner.z - distFromEdge; z++) {
				if (random.nextFloat() > treeChance) continue;
				
				for (int i = 0; i <= maxTreeHeight; i++) {
					int y = terrainGrounds[x][z] + i;
					if (y >= Options.buildHeight) break;

					worldBlocks[x][y][z] = Block.LOG;

					// Add leaves
					if (i > maxTreeHeight - leafHeight) {
						for (int a = -1; a <= 1; a++)
							for (int b = -1; b <= 1; b++) {
								// Don't replace the trunk
								if (i < maxTreeHeight && a == 0 && b == 0) continue;
								
								// Top of tree: skip edges
								if (i == maxTreeHeight && a != 0 && b != 0) continue;

								worldBlocks[x + a][y][z + b] = Block.LEAF;
							}
					}
				}
			}
		}
	}

}
