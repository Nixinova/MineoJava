package com.nixinova;

public class Conversion {
	public static final int PX_PER_BLOCK = 8;

	public static int pxToBlock(double px) {
		return (int) px * PX_PER_BLOCK;
	}

	public static int txToBlock(int px) {
		return px * PX_PER_BLOCK;
	}

	public static double blockToPx(int block) {
		return (double) block * PX_PER_BLOCK;
	}

}
