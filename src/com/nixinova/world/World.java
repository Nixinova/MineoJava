package com.nixinova.world;

import java.util.Random;

import com.nixinova.graphics.Render;
import com.nixinova.graphics.Textures;
import com.nixinova.input.Coord;
import com.nixinova.readwrite.Options;

public class World {
	
	public Coord lookingAtBlock;

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
		this.lookingAtBlock = new Coord();
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
