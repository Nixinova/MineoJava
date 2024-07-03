package com.nixinova;

public class Conversion {
	public static final int PX_PER_BLOCK = 8;

	public static int pxToCoord(int px) {
		// Implement two's complement to make mapping of 0..7->0, -1..-8->-1
		return px >= 0 ? px / PX_PER_BLOCK : (px - PX_PER_BLOCK + 1) / PX_PER_BLOCK;
	}

}
