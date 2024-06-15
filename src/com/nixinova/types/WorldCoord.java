package com.nixinova.types;

import com.nixinova.world.Blocks;

public class WorldCoord {
	
	private Coord worldPos;
	
	public WorldCoord() {
		this.worldPos = new Coord();
	}
	
	public void setWorldCoords(double x, double y, double z) {
		this.worldPos = new Coord(x, y, z);
	}
	
	public void setPxCoords(int x, int y, int z) {
		this.worldPos = new Coord(x, y, z);
	}
	
	public void setBlockCoords(int x, int y, int z) {
		Coord pxCoord = Blocks.blockCoordsToWorldPx(x, y, z);
		this.worldPos = new Coord(pxCoord.x, pxCoord.y, pxCoord.z);
	}
	
	public Coord getWorldCoords() {
		return this.worldPos;
	}
	
	public Coord getPxCoords() {
		return new Coord((int) worldPos.x, (int) worldPos.y, (int) worldPos.z);
	}
	
	public BlockCoord getBlockCoords() {
		return Blocks.worldPxToBlockCoords(worldPos);
	}

}
