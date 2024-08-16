package com.nixinova.coords;

import com.nixinova.Vector3;

public class TxCoord extends BaseCoord<Short> {

	public static class Vector extends Vector3<Short> {
		public Vector(short x, short y, short z) {
			super(x, y, z);
		}
	}

	public short x, y, z;

	public TxCoord() {
		this.x = this.y = this.z = 0;
	}

	public TxCoord(int x, int y, int z) {
		this.x = (short) x;
		this.y = (short) y;
		this.z = (short) z;
	}

	public Coord3 toCoord() {
		return Coord3.fromTx(this);
	}

}
