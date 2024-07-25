package com.nixinova.blocks;

import com.nixinova.coords.BlockCoord;

public class HoveredBlock {

	/** The coordinate of the block being looked at */
	public BlockCoord hoveredBlock;
	/** The coordinate a new block would be placed in */
	public BlockCoord adjacentBlock;

	public HoveredBlock(BlockCoord hovered, BlockCoord adjacent) {
		this.hoveredBlock = hovered;
		this.adjacentBlock = adjacent;
	}

}
