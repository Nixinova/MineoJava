package com.nixinova.world;

import com.nixinova.coords.BlockCoord;
import com.nixinova.coords.Coord;

public class Conversion {
	public static final int PX_PER_BLOCK = 8;

	public static int pxToCoord(int px) {
		// Implement two's complement to make mapping of 0..7->0, -1..-8->-1
		return px >= 0 ? px / PX_PER_BLOCK : (px - PX_PER_BLOCK + 1) / PX_PER_BLOCK;
	}

	public static BlockCoord worldPxToBlockCoords(int pxX, int pxY, int pxZ) {
	    return new BlockCoord(pxToCoord(pxX), pxToCoord(pxY), pxToCoord(pxZ));
	}
	public static BlockCoord worldPxToBlockCoords(int pxX, int pxZ) {
		return worldPxToBlockCoords(pxX, 0, pxZ);
	}
	public static BlockCoord worldPxToBlockCoords(Coord coords) {
		return worldPxToBlockCoords((int) coords.x, (int) coords.y, (int) coords.z);
	}

	public static Coord blockCoordsToWorldPx(int blockX, int blockY, int blockZ) {
        int pxX = blockX * PX_PER_BLOCK + PX_PER_BLOCK / 2;
        int pxY = blockY * PX_PER_BLOCK + PX_PER_BLOCK / 2;
        int pxZ = blockZ * PX_PER_BLOCK + PX_PER_BLOCK / 2;
        return new Coord(pxX, pxY, pxZ);
	}
	public static Coord blockCoordsToWorldPx(int blockX, int blockZ) {
        return blockCoordsToWorldPx(blockX, 0, blockZ);
	}

}
