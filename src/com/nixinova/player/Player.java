package com.nixinova.player;

import com.nixinova.coords.BlockCoord;
import com.nixinova.coords.Coord3;
import com.nixinova.world.World;

public class Player {
	public static final double PLAYER_HEIGHT = 1.8;

	private Coord3 position;
	private BlockCoord lookingAtBlock;

	public Player() {
		this.position = new Coord3();
		this.lookingAtBlock = null;
	}

	public Coord3 getPosition() {
		return this.position;
	}

	public void updatePosition(Coord3 footPosition) {
		this.position = footPosition;
	}

	public BlockCoord getLookingAt() {
		return this.lookingAtBlock;
	}

	public void setLookingAt(BlockCoord coord) {
		this.lookingAtBlock = coord;
	}

	public void setLookingAt(int x, int y, int z) {
		this.lookingAtBlock.x = x;
		this.lookingAtBlock.y = y;
		this.lookingAtBlock.z = z;
	}

	public boolean isWithinWorld(World world) {
		BlockCoord pos = this.position.toBlock();
		BlockCoord min = world.minCorner;
		BlockCoord max = world.maxCorner;
		return pos.x >= min.x && pos.x <= max.x && pos.y >= min.y && pos.y <= max.y && pos.z >= min.z && pos.z <= max.z;
	}

}
