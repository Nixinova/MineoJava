package com.nixinova;

public class Conversion {
	public static final int PX_PER_BLOCK = 8;

	/* Implement two's complement to make mapping of 0..7->0, -1..-8->-1 */
	public static int txToBlockTwosComp(int tx) {
		if (tx >= 0)
			return tx / PX_PER_BLOCK;
		else
			return (tx - (PX_PER_BLOCK - 1)) / PX_PER_BLOCK;
	}
	
	// General

	public static int pxToBlock(double px) {
		return (int) px / PX_PER_BLOCK;
	}

	public static double pxToSubBlock(double px) {
		return px * PX_PER_BLOCK;
	}

	public static int txToBlock(int tx) {
		return tx / PX_PER_BLOCK;
	}

	public static double blockToPx(int block) {
		return (double) block * PX_PER_BLOCK;
	}

	public static double subBlockToPx(double block) {
		return block * PX_PER_BLOCK;
	}

}
