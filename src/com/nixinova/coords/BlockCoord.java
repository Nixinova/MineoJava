package com.nixinova.coords;

import com.nixinova.world.Conversion;

public class BlockCoord {

	public int x;
	public int y;
	public int z;
	
	private final int per = Conversion.PX_PER_BLOCK;

	public BlockCoord() {
		this.x = this.y = this.z = 0;
	}

	public BlockCoord(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public PxCoord toPxCoord() {
		return new PxCoord(this.x * per, this.y * per, this.z * per);
	}

	public TxCoord toTxCoord() {
		return new TxCoord(this.x * per, this.y * per, this.z * per);
	}

	public BlockCoord toBlockCoord() {
		return new BlockCoord(this.x * per, this.y * per, this.z * per);
	}

}
