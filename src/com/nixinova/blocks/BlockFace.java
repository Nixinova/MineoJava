package com.nixinova.blocks;

import com.nixinova.graphics.Texture;

public enum BlockFace {
	TOP, BOTTOM, LEFT, RIGHT, FRONT, BACK;
	
	public static BlockFace getFromTx(int txX, int txY, int txZ) {
		final int size = Texture.SIZE;

		// Get 0..7-ranged texel IDs
		int texIdX = txX % size;
		int texIdY = txY % size;
		int texIdZ = txZ % size;

		// Set block's face
		BlockFace face = null;
		if (texIdX == 0)
			face = RIGHT;
		else if (texIdX == size - 1)
			face = LEFT;
		else if (texIdY == 0)
			face = BOTTOM;
		else if (texIdY == size - 1)
			face = TOP;
		else if (texIdZ == 0)
			face = BACK;
		else if (texIdZ == size - 1)
			face = FRONT;

		return face;
	}


}
