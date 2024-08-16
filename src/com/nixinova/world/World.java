package com.nixinova.world;

import java.util.Random;

import com.nixinova.blocks.Block;
import com.nixinova.blocks.BlockFace;
import com.nixinova.coords.BlockCoord;
import com.nixinova.graphics.Render;
import com.nixinova.options.Options;

public class World {
	/** Inclusive (>=) */
	public final BlockCoord minCorner;
	/** Exclusive (<) */
	public final BlockCoord maxCorner;

	private Render[][][] blockTextures;

	public World() {
		this.minCorner = new BlockCoord(0, 0, 0);
		this.maxCorner = new BlockCoord(Options.worldSize, Options.buildHeight, Options.worldSize);

		this.mapBlockTextures();
	}

	public boolean isWithinWorld(short blockX, short blockY, short blockZ) {
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
	public int getMinGroundY(short blockX, short blockZ) {
		for (byte i = 0; i < Options.buildHeight; i++) {
			if (this.getTextureAt(blockX, i, blockZ) == null) {
				return i - 1;
			}
		}
		return -1;
	}

	public boolean isExposed(short blockX, short blockY, short blockZ) {
		for (byte x = -1; x <= 1; x += 2) {
			for (byte y = -1; y <= 1; y += 2) {
				for (byte z = -1; z <= 1; z += 2) {
					if (isAir((short) (blockX + x), (short) (blockY + y), (short) (blockZ + z)))
						return true;
				}
			}
		}
		return false;
	}

	public boolean isFaceExposed(BlockFace face, short blockX, short blockY, short blockZ) {
		var offset = face.getOffset();
		BlockCoord adjacent = new BlockCoord(blockX, blockY, blockZ).applyVector(offset);
		return isAir(adjacent.x, adjacent.y, adjacent.z);
	}

	public boolean isAir(short blockX, short blockY, short blockZ) {
		boolean isOutsideWorld = !isWithinWorld(blockX, blockY, blockZ);
		boolean isAir = getTextureAt(blockX, blockY, blockZ) == Block.AIR.getTexture();
		return isOutsideWorld || isAir;
	}

	public boolean isAir(BlockCoord block) {
		return isAir(block.x, block.y, block.z);
	}

	public Render getTextureAt(short blockX, short blockY, short blockZ) {
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
		this.blockTextures = new Render[this.maxCorner.x + 1][this.maxCorner.y + 1][this.maxCorner.z + 1];

		Random random = new Random(Options.seed);

		for (short x = 0; x < this.maxCorner.x; x++) {
			for (short z = 0; z < this.maxCorner.z; z++) {
				int localGroundY = Options.groundLevel;

				for (short y = 0; y < this.maxCorner.y; y++) {
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
