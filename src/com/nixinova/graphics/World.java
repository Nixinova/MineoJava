package com.nixinova.graphics;

import java.util.Random;

import com.nixinova.readwrite.Options;

public class World {
	
	public static final Render[] Blocks = new Render[] {
		Textures.bedrock,
		Textures.grass,
		Textures.dirt,
		Textures.stone,
	};

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
	}

	public Render getTextureAt(int blockX, int blockZ) {
		final int wR = Options.worldRepeat;
		int absBlockX = (blockX % wR) + wR;
		int absBlockZ = (blockZ % wR) + wR;
		return this.blockTextures[absBlockX][absBlockZ];
	}

	public void setTextureAt(int blockX, int blockZ, Render texture) {
		int absBlockX = (blockX % wR) + wR;
		int absBlockZ = (blockZ % wR) + wR;
		this.blockTextures[absBlockX][absBlockZ] = texture;
	}

	private void mapBlockTextures() {
		final int size = Options.worldRepeat * 2;
		this.blockTextures = new Render[size][size];

		Random random = new Random(Options.seed);

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				blockTextures[i][j] = this.groundBlocks[random.nextInt(this.groundBlocks.length)];
			}
		}

	}

}
