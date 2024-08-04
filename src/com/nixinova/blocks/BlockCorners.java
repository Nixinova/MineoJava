package com.nixinova.blocks;

import com.nixinova.coords.SubBlockCoord;

public class BlockCorners {

	public int blockX, blockY, blockZ;
	public SubBlockCoord minmin, minmax, maxmin, maxmax;

	public BlockCorners(int blockX, int blockY, int blockZ, BlockFace face) {
		this.blockX = blockX;
		this.blockY = blockY;
		this.blockZ = blockZ;
		loadCornersForFace(face);
	}

	public SubBlockCoord[] toArray() {
		// Important: order must be --, -+, ++, +-
		return new SubBlockCoord[] { minmin, minmax, maxmax, maxmin };
	}

	private void loadCornersForFace(BlockFace face) {
		final double offset = 0.999;
		switch (face) {
			// Note: ()-parts are the unchanging axis
			case XMIN -> {
				// X=+0; four corners of Y/Z
				minmin = new SubBlockCoord((blockX), blockY, blockZ);
				minmax = new SubBlockCoord((blockX), blockY, blockZ + offset);
				maxmin = new SubBlockCoord((blockX), blockY + offset, blockZ);
				maxmax = new SubBlockCoord((blockX), blockY + offset, blockZ + offset);
			}
			case XMAX -> {
				// X=+1; four corners of Y/Z
				minmin = new SubBlockCoord((blockX + 1), blockY, blockZ);
				minmax = new SubBlockCoord((blockX + 1), blockY, blockZ + offset);
				maxmin = new SubBlockCoord((blockX + 1), blockY + offset, blockZ);
				maxmax = new SubBlockCoord((blockX + 1), blockY + offset, blockZ + offset);
			}
			case YMIN -> {
				// Y=+0; four corners of X/Z
				minmin = new SubBlockCoord(blockX, (blockY), blockZ);
				minmax = new SubBlockCoord(blockX, (blockY), blockZ + offset);
				maxmin = new SubBlockCoord(blockX + offset, (blockY), blockZ);
				maxmax = new SubBlockCoord(blockX + offset, (blockY), blockZ + offset);
			}
			case YMAX -> {
				// Y=+1; four corners of X/Z
				minmin = new SubBlockCoord(blockX, (blockY + 1), blockZ);
				minmax = new SubBlockCoord(blockX, (blockY + 1), blockZ + offset);
				maxmin = new SubBlockCoord(blockX + offset, (blockY + 1), blockZ);
				maxmax = new SubBlockCoord(blockX + offset, (blockY + 1), blockZ + offset);
			}
			case ZMIN -> {
				// Z=+0; four corners of X/Y
				minmin = new SubBlockCoord(blockX, blockY, (blockZ));
				minmax = new SubBlockCoord(blockX, blockY + offset, (blockZ));
				maxmax = new SubBlockCoord(blockX + offset, blockY + offset, (blockZ));
				maxmin = new SubBlockCoord(blockX + offset, blockY, (blockZ));
			}
			case ZMAX -> {
				// Z=+1; four corners of X/Y
				minmin = new SubBlockCoord(blockX, blockY, (blockZ + 1));
				minmax = new SubBlockCoord(blockX, blockY + offset, (blockZ + 1));
				maxmax = new SubBlockCoord(blockX + offset, blockY + offset, (blockZ + 1));
				maxmin = new SubBlockCoord(blockX + offset, blockY, (blockZ + 1));
			}
		}
	}

}
