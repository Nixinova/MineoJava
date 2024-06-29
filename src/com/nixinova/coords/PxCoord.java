package com.nixinova.coords;

import com.nixinova.world.Conversion;

public class PxCoord {

	public double x;
	public double y;
	public double z;

	private final int per = Conversion.PX_PER_BLOCK;

	public PxCoord() {
		this.x = this.y = this.z = 0.0;
	}

	public PxCoord(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public PxCoord toPxCoord() {
		return new PxCoord(this.x, this.y, this.z);
	}

	public TxCoord toTxCoord() {
		return new TxCoord((int) this.x, (int) this.y, (int) this.z);
	}

	public BlockCoord toBlockCoord() {
		return new BlockCoord((int) this.x / per, (int) this.y / per, (int) this.z / per);
	}
	
	public double value() {
		return this.x;
	}

}
