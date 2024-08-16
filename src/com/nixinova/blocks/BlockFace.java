package com.nixinova.blocks;

import java.util.ArrayList;
import java.util.List;

import com.nixinova.Vector3;
import com.nixinova.graphics.Texture;

public enum BlockFace {
	XMIN, XMAX, YMIN, YMAX, ZMIN, ZMAX;

	public static List<BlockFace> getFacesFromTx(int txX, int txY, int txZ) {
		final int size = Texture.SIZE;

		// Get 0..7-ranged texel IDs
		int texIdX = txX % size;
		int texIdY = txY % size;
		int texIdZ = txZ % size;

		// Set block faces
		List<BlockFace> faces = new ArrayList<>();
		if (texIdX == 0)
			faces.add(XMIN);
		if (texIdX == size - 1)
			faces.add(XMAX);
		if (texIdY == 0)
			faces.add(YMIN);
		if (texIdY == size - 1)
			faces.add(YMAX);
		if (texIdZ == 0)
			faces.add(ZMIN);
		if (texIdZ == size - 1)
			faces.add(ZMAX);

		return faces;
	}

	public Vector3.SmallInt getOffset() {
		byte x = 0;
		byte y = 0;
		byte z = 0;

		switch (this) {
			case XMAX -> x++;
			case XMIN -> x--;
			case YMAX -> y++;
			case YMIN -> y--;
			case ZMAX -> z++;
			case ZMIN -> z--;
		}

		return new Vector3.SmallInt(x, y, z);
	}

}
