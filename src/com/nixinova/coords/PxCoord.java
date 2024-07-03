package com.nixinova.coords;

import com.nixinova.Conversion;

public class PxCoord implements ICoord<Double> {

	public double x;
	public double y;
	public double z;

	private static final int PER = Conversion.PX_PER_BLOCK;

	public PxCoord() {
		this.x = this.y = this.z = 0.0;
	}

	public PxCoord(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public PxCoord toPxCoord() {
		return this;
	}

	public TxCoord toTxCoord() {
		return new TxCoord((int) this.x, (int) this.y, (int) this.z);
	}

	public BlockCoord toBlockCoord() {
		return new BlockCoord((int) this.x / PER, (int) this.y / PER, (int) this.z / PER);
	}

	public SubBlockCoord toSubBlockCoord() {
		return new SubBlockCoord(this.x / PER, this.y / PER, this.z / PER);
	}
	
	public Double value() {
		return this.x;
	}

}
