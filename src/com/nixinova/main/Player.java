package com.nixinova.main;

import com.nixinova.readwrite.Options;
import com.nixinova.types.BlockCoord;
import com.nixinova.types.Coord;
import com.nixinova.types.WorldCoord;

public class Player {

	private WorldCoord playerPosition;
	private BlockCoord lookingAtBlock;
	
	public Player() {
		this.playerPosition = new WorldCoord();
		this.lookingAtBlock = new BlockCoord();
	}

	public Coord getPlayerPos() {
		return this.playerPosition.getWorldCoords();
	}

	public void setPlayerPos(double x, double y, double z) {
		this.playerPosition.setWorldCoords(x, y, z);
	}

	public void setPlayerPos(Coord coords) {
		this.setPlayerPos(coords.x, coords.y, coords.z);
	}

	public Coord getPlayerPxPos() {
		return this.playerPosition.getPxCoords();
	}

	public void setPlayerPxPos(int x, int y, int z) {
		this.playerPosition.setPxCoords(x, y, z);
	}

	public void setPlayerPxPos(Coord coords) {
		this.setPlayerPxPos((int) coords.x, (int) coords.y, (int) coords.z);
	}

	public BlockCoord getPlayerBlockPos() {
		return this.playerPosition.getBlockCoords();
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
