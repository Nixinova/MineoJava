package com.nixinova.world;

import java.util.Random;

import com.nixinova.graphics.Render;
import com.nixinova.graphics.Textures;
import com.nixinova.readwrite.Options;
import com.nixinova.types.BlockCoord;
import com.nixinova.types.Coord;
import com.nixinova.types.WorldCoord;

public class World {

	private WorldCoord playerPosition;
	private BlockCoord lookingAtBlock;

	private final int wR;
	private final Render[] groundBlocks = new Render[] {
		Textures.grass,
		Textures.dirt,
		Textures.stone,
	};

	private Render[][] blockTextures;

	public World() {
		this.wR = Options.worldRepeat;

		this.mapBlockTextures();
		this.playerPosition = new WorldCoord();
		this.lookingAtBlock = new BlockCoord();
	}

	public Coord getPlayerPos() {
		return this.playerPosition.getWorldCoords();
	}
	public void setPlayerPos(double x, double y, double z) {
		this.playerPosition.setWorldCoords(x, y, z);
	}
	public void setPlayerPos(Coord coords) {
		this.setPlayerPos(coords.x, coords.y, coords.z);
	}

	public Coord getPlayerPxPos() {
		return this.playerPosition.getPxCoords();
	}
	public void setPlayerPxPos(int x, int y, int z) {
		this.playerPosition.setPxCoords(x, y, z);
	}
	public void setPlayerPxPos(Coord coords) {
		this.setPlayerPxPos((int) coords.x, (int) coords.y, (int) coords.z);
	}

	public BlockCoord getPlayerBlockPos() {
		return this.playerPosition.getBlockCoords();
	}
	public void setPlayerBlockPos(int x, int y, int z) {
		this.playerPosition.setBlockCoords(x, y, z);
	}
	public void setPlayerBlockPos(BlockCoord coords) {
		this.setPlayerBlockPos(coords.x, coords.y, coords.z);
	}

	public BlockCoord getLookingAt() {
		return this.lookingAtBlock;
	}
	public void setLookingAt(int x, int y, int z) {
		this.lookingAtBlock = new BlockCoord(x, y, z);
	}
	public void setLookingAt(int x, int z) {
		this.setLookingAt(x, 0, z);
	}

	public Render getTextureAt(int blockX, int blockZ) {
		return this.blockTextures[toBlockIndex(blockX)][toBlockIndex(blockZ)];
	}
	public void setTextureAt(int blockX, int blockZ, Render texture) {
		this.blockTextures[toBlockIndex(blockX)][toBlockIndex(blockZ)] = texture;
	}

	private void mapBlockTextures() {
		final int size = Options.worldRepeat * 2;
		this.blockTextures = new Render[size][size];

		Random random = new Random(Options.seed);

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				Render texture;
				if (toBlockCoord(i) == 0 || toBlockCoord(j) == 0)
					// Bedrock along world origin
					texture = Textures.bedrock;
				else
					texture = this.groundBlocks[random.nextInt(this.groundBlocks.length)];
				blockTextures[i][j] = texture;
			}
		}
	}

	private int toBlockIndex(int blockCoord) {
		return (blockCoord % wR) + wR;
	}
	
	private int toBlockCoord(int blockIndex) {
		return blockIndex - wR;
	}

}
