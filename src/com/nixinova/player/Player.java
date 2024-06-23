package com.nixinova.player;

import com.nixinova.coords.BlockCoord;
import com.nixinova.coords.PxCoord;
import com.nixinova.coords.TxCoord;
import com.nixinova.coords.WorldCoord;
import com.nixinova.readwrite.Options;
import com.nixinova.world.Conversion;

public class Player {
	public static final int PLAYER_HEIGHT = 2 * Conversion.PX_PER_BLOCK;

	private WorldCoord playerPosition;
	private BlockCoord lookingAtBlock;
	
	public Player() {
		this.playerPosition = new WorldCoord();
		this.lookingAtBlock = new BlockCoord();
	}

	public PxCoord getPlayerPxPos() {
		return this.playerPosition.getCoords().toPxCoord();
	}

	public void setPlayerPxPos(double x, double y, double z) {
		this.playerPosition.setPxCoords(x, y, z);
	}

	public void setPlayerPxPos(PxCoord coords) {
		this.setPlayerPxPos(coords.x, coords.y, coords.z);
	}

	public TxCoord getPlayerTxPos() {
		return this.playerPosition.getCoords().toTxCoord();
	}

	public void setPlayerTxPos(int x, int y, int z) {
		this.playerPosition.setTxCoords(x, y, z);
	}

	public void setPlayerTxPos(TxCoord coords) {
		this.setPlayerTxPos(coords.x, coords.y, coords.z);
	}

	public BlockCoord getPlayerBlockPos() {
		return this.playerPosition.getCoords().toBlockCoord();
	}

	public void setPlayerBlockPos(int x, int y, int z) {
		this.playerPosition.setBlockCoords(x, y, z);
	}

	public void setPlayerBlockPos(BlockCoord coords) {
		this.setPlayerBlockPos(coords.x, coords.y, coords.z);
	}

	public BlockCoord getLookingAt() {
		return this.lookingAtBlock;
	}

	public void setLookingAt(int x, int y, int z) {
		this.lookingAtBlock = new BlockCoord(x, y, z);
	}
	
	public boolean isWithinWorld() {
		BlockCoord coord = this.getPlayerBlockPos();
		int size = Options.worldSize;
		return coord.x >= -size && coord.x <= size && coord.y >= 0 && coord.z >= -size && coord.z <= size;
	}

}
