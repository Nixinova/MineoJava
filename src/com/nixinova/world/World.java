package com.nixinova.world;

import java.util.Random;

import com.nixinova.graphics.Render;
import com.nixinova.readwrite.Options;
import com.nixinova.types.BlockCoord;
import com.nixinova.types.Coord;
import com.nixinova.types.WorldCoord;

public class World {

	private WorldCoord playerPosition;
	private BlockCoord lookingAtBlock;

	private final int wR;
	private final Render[] groundBlocks = new Render[] {
		Block.GRASS.getTexture(),
		Block.DIRT.getTexture(),
		Block.STONE.getTexture(),
	};

	private Render[][][] blockTextures;

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

	public Render getTextureAt(int blockX, int blockY, int blockZ) {
		BlockCoord coordI = toBlockIndex(new BlockCoord(blockX, blockY, blockZ));
		Render texture = this.blockTextures[coordI.x][coordI.y][coordI.z];
		return texture;
	}
	public void setTextureAt(int blockX, int blockY, int blockZ, Render texture) {
		BlockCoord coordI = toBlockIndex(new BlockCoord(blockX, blockY, blockZ));
		this.blockTextures[coordI.x][coordI.y][coordI.z] = texture;
	}

	private void mapBlockTextures() {
		final int size = Options.worldRepeat * 2;
		this.blockTextures = new Render[size][size][size];

		Random random = new Random(Options.seed);

		// TODO: 3D coord-based: y=0 place block, otherwise AIR
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				for (int z = 0; z < size; z++) {
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
		return (blockCoord % wR) + wR;
	}
	private BlockCoord toBlockIndex(BlockCoord blockCoord) {
		return new BlockCoord(toBlockIndex(blockCoord.x), toBlockIndex(blockCoord.y), toBlockIndex(blockCoord.z));
	}
	
	private int toBlockCoord(int blockIndex) {
		return blockIndex - wR;
	}
	private BlockCoord toBlockCoord(BlockCoord indexCoord) {
		return new BlockCoord(toBlockCoord(indexCoord.x), toBlockCoord(indexCoord.y), toBlockCoord(indexCoord.z));
	}

}
