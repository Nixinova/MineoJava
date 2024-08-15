package com.nixinova.player;

import com.nixinova.blocks.HoveredBlock;
import com.nixinova.coords.BlockCoord;
import com.nixinova.coords.Coord3;
import com.nixinova.coords.SubBlockCoord;
import com.nixinova.world.World;

public class Player {
	public static final double PLAYER_HEIGHT = 1.8;
	public static final double PLAYER_WIDTH = 0.8;

	private Coord3 position;
	private HoveredBlock lookingAtBlock;

	public Player(Coord3 startPosition) {
		this.position = startPosition;
		this.lookingAtBlock = new HoveredBlock(null, null);
	}

	public Coord3 getPosition() {
		return this.position;
	}

	public void updatePosition(Coord3 footPosition) {
		this.position = footPosition;
	}

	public HoveredBlock getLookingAt() {
		return this.lookingAtBlock;
	}

	public void setLookingAt(HoveredBlock result) {
		this.lookingAtBlock = result;
	}

	public boolean isWithinWorld(World world) {
		SubBlockCoord pos = this.position.toSubBlock();
		BlockCoord min = world.minCorner;
		BlockCoord max = world.maxCorner;
		return pos.x >= min.x && pos.x <= max.x && pos.y >= min.y && pos.y <= max.y && pos.z >= min.z && pos.z <= max.z;
	}

}
