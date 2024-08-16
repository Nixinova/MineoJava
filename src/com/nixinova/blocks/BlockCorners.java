package com.nixinova.blocks;

import com.nixinova.coords.SubBlockCoord;

/*
 * Corners of a block:
 * A--B
 * |  |
 * C--D
 */

public class BlockCorners {

	public short blockX, blockY, blockZ;
	public SubBlockCoord cornerA, cornerB, cornerC, cornerD;

	public BlockCorners(short blockX, short blockY, short blockZ, BlockFace face) {
		this.blockX = blockX;
		this.blockY = blockY;
		this.blockZ = blockZ;
		this.loadCornersForFace(face);
	}

	public SubBlockCoord[] toArray() {
		return abcdToArray(cornerA, cornerB, cornerC, cornerD);
	}

	private void loadCornersForFace(BlockFace face) {
		final int offset = 1;
		switch (face) {
			// Note: ()-parts are the unchanging axis
			case XMIN -> {
				// X=+0; four corners of Y/Z
				cornerA = new SubBlockCoord((blockX), blockY, blockZ);
				cornerB = new SubBlockCoord((blockX), blockY, blockZ + offset);
				cornerC = new SubBlockCoord((blockX), blockY + offset, blockZ);
				cornerD = new SubBlockCoord((blockX), blockY + offset, blockZ + offset);
			}
			case XMAX -> {
				// X=+1; four corners of Y/Z
				cornerA = new SubBlockCoord((blockX + 1), blockY, blockZ);
				cornerB = new SubBlockCoord((blockX + 1), blockY, blockZ + offset);
				cornerC = new SubBlockCoord((blockX + 1), blockY + offset, blockZ);
				cornerD = new SubBlockCoord((blockX + 1), blockY + offset, blockZ + offset);
			}
			case YMIN -> {
				// Y=+0; four corners of X/Z
				cornerA = new SubBlockCoord(blockX, (blockY), blockZ);
				cornerB = new SubBlockCoord(blockX, (blockY), blockZ + offset);
				cornerC = new SubBlockCoord(blockX + offset, (blockY), blockZ);
				cornerD = new SubBlockCoord(blockX + offset, (blockY), blockZ + offset);
			}
			case YMAX -> {
				// Y=+1; four corners of X/Z
				cornerA = new SubBlockCoord(blockX, (blockY + 1), blockZ);
				cornerB = new SubBlockCoord(blockX, (blockY + 1), blockZ + offset);
				cornerC = new SubBlockCoord(blockX + offset, (blockY + 1), blockZ);
				cornerD = new SubBlockCoord(blockX + offset, (blockY + 1), blockZ + offset);
			}
			case ZMIN -> {
				// Z=+0; four corners of X/Y
				cornerA = new SubBlockCoord(blockX, blockY, (blockZ));
				cornerB = new SubBlockCoord(blockX, blockY + offset, (blockZ));
				cornerD = new SubBlockCoord(blockX + offset, blockY + offset, (blockZ));
				cornerC = new SubBlockCoord(blockX + offset, blockY, (blockZ));
			}
			case ZMAX -> {
				// Z=+1; four corners of X/Y
				cornerA = new SubBlockCoord(blockX, blockY, (blockZ + 1));
				cornerB = new SubBlockCoord(blockX, blockY + offset, (blockZ + 1));
				cornerD = new SubBlockCoord(blockX + offset, blockY + offset, (blockZ + 1));
				cornerC = new SubBlockCoord(blockX + offset, blockY, (blockZ + 1));
			}
		}
	}

	protected static SubBlockCoord[] abcdToArray(SubBlockCoord a, SubBlockCoord b, SubBlockCoord c, SubBlockCoord d) {
		// Important: order must be A, B, D, C
		return new SubBlockCoord[] { a, b, d, c };
	}

}
