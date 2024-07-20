package com.nixinova.coords;

public class TxCoord implements ICoord<Integer> {
	
	public int x;
	public int y;
	public int z;
	
	public TxCoord() {
		this.x = this.y = this.z = 0;
	}
	
	public TxCoord(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Coord3 toCoord() {
		return Coord3.fromTx(this);
	}
	
	public Integer value() {
		return this.x;
	}

}
