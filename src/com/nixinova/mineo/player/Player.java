package com.nixinova.mineo.player;

import com.nixinova.mineo.maths.coords.BlockCoord;
import com.nixinova.mineo.maths.coords.Coord3;
import com.nixinova.mineo.maths.coords.SubBlockCoord;
import com.nixinova.mineo.options.Options;
import com.nixinova.mineo.world.World;
import com.nixinova.mineo.world.blocks.HoveredBlock;

public class Player {
	public static final double PLAYER_HEIGHT = 1.6;
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
		BlockCoord min = World.minCorner;
		BlockCoord max = World.maxCorner;
		return pos.x >= min.x && pos.x <= max.x && pos.y >= min.y && pos.y <= max.y && pos.z >= min.z && pos.z <= max.z;
	}

	public boolean isUnderground(World world) {
		if (!isWithinWorld(world))
			return false;

		BlockCoord pos = this.position.toBlock();
		int playerHeadPosY = (int) (pos.y + PLAYER_HEIGHT);
		for (int y = playerHeadPosY; y < Options.buildHeight; y++) {
			if (!world.isAir(pos.x, y, pos.z))
				return true;
		}
		return false;
	}

}
