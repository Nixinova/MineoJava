package com.nixinova.coords;

public class BlockCoord implements ICoord<Integer> {

	public int x;
	public int y;
	public int z;

	public BlockCoord() {
		this.x = this.y = this.z = 0;
	}

	public BlockCoord(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Coord3 toCoord() {
		return Coord3.fromBlock(this);
	}

	public Integer value() {
		return this.x;
	}

}
