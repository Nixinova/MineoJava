package com.nixinova.coords;

import com.nixinova.world.Conversion;

public class TxCoord {
	
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
	
	public PxCoord toPxCoord() {
		return new PxCoord(this.x, this.y, this.z);
	}
	
	public TxCoord toTxCoord() {
		return new TxCoord(this.x, this.y, this.z);
	}
	
	public BlockCoord toBlockCoord() {
		int per = Conversion.PX_PER_BLOCK;
		return new BlockCoord(this.x / per, this.y / per, this.z / per);
	}
	
	public double value() {
		return this.x;
	}

}
