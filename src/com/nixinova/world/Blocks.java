package com.nixinova.world;

import com.nixinova.graphics.Render;
import com.nixinova.graphics.Textures;
import com.nixinova.input.Coord;

public class Blocks {
	public static final int PX_PER_BLOCK = 8;

	public static final Render[] BLOCKS = new Render[] {
		Textures.sky,
		Textures.bedrock,
		Textures.grass,
		Textures.dirt,
		Textures.stone,
	};
	
	public static int pxToCoord(int px) {
		// Implement two's complement to make mapping of 0..7->0, -1..-8->-1
		return px >= 0 ? px / PX_PER_BLOCK : (px - PX_PER_BLOCK + 1) / PX_PER_BLOCK;
	}

	public static Coord worldPxToBlockCoords(int pxX, int pxY, int pxZ) {
	    return new Coord(pxToCoord(pxX), pxToCoord(pxY), pxToCoord(pxZ));
	}

	public static Coord worldPxToBlockCoords(int pxX, int pxZ) {
		return worldPxToBlockCoords(pxX, 0, pxZ);
	}

	public static Coord blockCoordsToWorldPx(int blockX, int blockZ) {
        int pxX = blockX * PX_PER_BLOCK + PX_PER_BLOCK / 2;
        int pxZ = blockZ * PX_PER_BLOCK + PX_PER_BLOCK / 2;
        return new Coord(pxX, 0, pxZ);
	}

}
