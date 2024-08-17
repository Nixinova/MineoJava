package com.nixinova.blocks;

import com.nixinova.coords.SubBlockCoord;

/*
 * Corners of a block:
 * 
 * A--B
 * |  |
 * C--D
 */

public class BlockCorners {

	public int blockX, blockY, blockZ;
	public SubBlockCoord cornerA, cornerB, cornerC, cornerD;

	public BlockCorners(int blockX, int blockY, int blockZ, BlockFace face) {
		this.blockX = blockX;
		this.blockY = blockY;
		this.blockZ = blockZ;
		this.loadCornersForFace(face);
	}

	public SubBlockCoord[] toArray() {
		return abcdToArray(this.cornerA, this.cornerB, this.cornerC, this.cornerD);
	}

	private void loadCornersForFace(BlockFace face) {
		final int offset = 1;

		this.cornerA = new SubBlockCoord(blockX, blockY, blockZ);
		this.cornerB = new SubBlockCoord(blockX, blockY, blockZ);
		this.cornerC = new SubBlockCoord(blockX, blockY, blockZ);
		this.cornerD = new SubBlockCoord(blockX, blockY, blockZ);

		switch (face) {
			// Note: ()-parts are the unchanging axis
			case XMIN -> {
				// X=+0; four corners of Y/Z
				this.cornerA.add(+0, 0, 0);
				this.cornerB.add(+0, 0, offset);
				this.cornerC.add(+0, offset, 0);
				this.cornerD.add(+0, offset, offset);
			}
			case XMAX -> {
				// X=+1; four corners of Y/Z
				this.cornerA.add(+1, 0, 0);
				this.cornerB.add(+1, 0, offset);
				this.cornerC.add(+1, offset, 0);
				this.cornerD.add(+1, offset, offset);
			}
			case YMIN -> {
				// Y=+0; four corners of X/Z
				this.cornerA.add(0, +0, 0);
				this.cornerB.add(0, +0, offset);
				this.cornerC.add(offset, +0, 0);
				this.cornerD.add(offset, +0, offset);
			}
			case YMAX -> {
				// Y=+1; four corners of X/Z
				// Y=+0; four corners of X/Z
				this.cornerA.add(0, +1, 0);
				this.cornerB.add(0, +1, offset);
				this.cornerC.add(offset, +1, 0);
				this.cornerD.add(offset, +1, offset);
			}
			case ZMIN -> {
				// Z=+0; four corners of X/Y
				this.cornerA.add(0, 0, +0);
				this.cornerB.add(0, offset, +0);
				this.cornerC.add(offset, 0, +0);
				this.cornerD.add(offset, offset, +0);
			}
			case ZMAX -> {
				// Z=+1; four corners of X/Y
				this.cornerA.add(0, 0, +1);
				this.cornerB.add(0, offset, +1);
				this.cornerC.add(offset, 0, +1);
				this.cornerD.add(offset, offset, +1);
			}
		}
	}

	protected static SubBlockCoord[] abcdToArray(SubBlockCoord a, SubBlockCoord b, SubBlockCoord c, SubBlockCoord d) {
		// Important: order must be A, B, D, C
		return new SubBlockCoord[] { a, b, d, c };
	}

}
