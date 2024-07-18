package com.nixinova.coords;

import com.nixinova.Conversion;

public class TxCoord implements ICoord<Integer> {
	
	public int x;
	public int y;
	public int z;
	
	private static final int PER = Conversion.PX_PER_BLOCK;
	
	public TxCoord() {
		this.x = this.y = this.z = 0;
	}
	
	public TxCoord(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Coord toCoord() {
		return Coord.fromTx(this);
	}
	
	public PxCoord toPxCoord() {
		return new PxCoord(this.x, this.y, this.z);
	}
	
	public TxCoord toTxCoord() {
		return this;
	}
	
	public BlockCoord toBlockCoord() {
		return new BlockCoord(this.x / PER, this.y / PER, this.z / PER);
	}
	
	public SubBlockCoord toSubBlockCoord() {
		return new SubBlockCoord((double) this.x / PER, (double) this.y / PER, (double) this.z / PER);
	}
	
	public Integer value() {
		return this.x;
	}

}
