package com.nixinova.player;

import com.nixinova.coords.BlockCoord;
import com.nixinova.coords.Coord;
import com.nixinova.readwrite.Options;
import com.nixinova.world.Conversion;

public class Player {
	public static final int PLAYER_HEIGHT = 2 * Conversion.PX_PER_BLOCK;

	public Coord position;

	private BlockCoord lookingAtBlock;

	public Player() {
		this.position = new Coord();
		this.lookingAtBlock = new BlockCoord();
	}

	public BlockCoord getLookingAt() {
		return this.lookingAtBlock;
	}

	public void setLookingAt(int x, int y, int z) {
		this.lookingAtBlock = new BlockCoord(x, y, z);
	}

	public boolean isWithinWorld() {
		BlockCoord coord = this.position.toBlock();
		int size = Options.worldSize;
		return coord.x >= -size && coord.x <= size && coord.y >= 0 && coord.z >= -size && coord.z <= size;
	}

}
