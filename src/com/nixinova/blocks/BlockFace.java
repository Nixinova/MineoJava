package com.nixinova.blocks;

import com.nixinova.graphics.Texture;

public class BlockFace {
	public boolean xMin, xMax;
	public boolean yMin, yMax;
	public boolean zMin, zMax;

	public BlockFace() {
		xMin = xMax = false;
		yMin = yMax = false;
		zMin = zMax = false;
	}

	public static BlockFace getFromTx(int txX, int txY, int txZ) {
		final int size = Texture.SIZE;

		// Get 0..7-ranged texel IDs
		int texIdX = txX % size;
		int texIdY = txY % size;
		int texIdZ = txZ % size;

		// Set block face
		BlockFace face = new BlockFace();
		if (texIdX == 0)
			face.xMin = true;
		if (texIdX == size - 1)
			face.xMax = true;
		if (texIdY == 0)
			face.yMin = true;
		if (texIdY == size - 1)
			face.yMax = true;
		if (texIdZ == 0)
			face.zMin = true;
		if (texIdZ == size - 1)
			face.zMax = true;

		return face;
	}

}
