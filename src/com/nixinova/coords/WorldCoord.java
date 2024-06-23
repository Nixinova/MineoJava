package com.nixinova.coords;

public class WorldCoord {
	
	private PxCoord worldPos;
	
	public WorldCoord() {
		this.worldPos = new PxCoord();
	}
	
	public void setPxCoords(double x, double y, double z) {
		this.worldPos = new PxCoord(x, y, z);
	}
	
	public void setTxCoords(int x, int y, int z) {
		this.worldPos = new PxCoord(x, y, z);
	}
	
	public void setBlockCoords(int x, int y, int z) {
		PxCoord pxCoord = new BlockCoord(x, y, z).toPxCoord();
		this.worldPos = new PxCoord(pxCoord.x, pxCoord.y, pxCoord.z);
	}
	
	public PxCoord getCoords() {
		return this.worldPos;
	}

}
