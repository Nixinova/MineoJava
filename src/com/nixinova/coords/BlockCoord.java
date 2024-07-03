package com.nixinova.coords;

import com.nixinova.world.Conversion;

public class BlockCoord implements ICoord<Integer> {

	public int x;
	public int y;
	public int z;

	private static final int PER = Conversion.PX_PER_BLOCK;

	public BlockCoord() {
		this.x = this.y = this.z = 0;
	}

	public BlockCoord(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public PxCoord toPxCoord() {
		return new PxCoord(this.x * PER, this.y * PER, this.z * PER);
	}

	public TxCoord toTxCoord() {
		return new TxCoord(this.x * PER, this.y * PER, this.z * PER);
	}

	public BlockCoord toBlockCoord() {
		return this;
	}

	public SubBlockCoord toSubBlockCoord() {
		return new SubBlockCoord(this.x, this.y, this.z);
	}

	public Integer value() {
		return this.x;
	}

}
