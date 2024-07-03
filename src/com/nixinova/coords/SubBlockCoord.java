package com.nixinova.coords;

import com.nixinova.Conversion;

public class SubBlockCoord implements ICoord<Double> {

	public double x;
	public double y;
	public double z;

	private static final int PER = Conversion.PX_PER_BLOCK;

	public SubBlockCoord() {
		this.x = this.y = this.z = 0.0;
	}

	public SubBlockCoord(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public PxCoord toPxCoord() {
		return new PxCoord(this.x * PER, this.y * PER, this.z * PER);
	}

	public TxCoord toTxCoord() {
		return new TxCoord((int) this.x * PER, (int) this.y * PER, (int) this.z * PER);
	}

	public BlockCoord toBlockCoord() {
		return new BlockCoord((int) this.x, (int) this.y, (int) this.z);
	}

	public SubBlockCoord toSubBlockCoord() {
		return this;
	}
	
	public Double value() {
		return this.x;
	}

}
