package com.nixinova.player;

import com.nixinova.coords.BlockCoord;
import com.nixinova.coords.Coord;
import com.nixinova.world.World;

public class Player {
	public static final int PLAYER_HEIGHT = 2;

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
