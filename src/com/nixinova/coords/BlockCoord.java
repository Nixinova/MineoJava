package com.nixinova.coords;

import com.nixinova.Vector3;

public class BlockCoord extends BaseCoord<Short> {

	public static class Vector extends Vector3<Short> {
		public Vector(short x, short y, short z) {
			super(x, y, z);
		}
	}

	public short x, y, z;

	public BlockCoord() {
		this.x = this.y = this.z = 0;
	}

	public BlockCoord(int x, int y, int z) {
		this.x = (short) x;
		this.y = (short) y;
		this.z = (short) z;
	}

	public Coord3 toCoord() {
		return Coord3.fromBlock(this);
	}

	public BlockCoord applyVector(Vector3<Short> vec) {
		return new BlockCoord(this.x + vec.x, this.y + vec.y, this.z + vec.z);
	}

	public BlockCoord applyVector(Vector3.SmallInt vec) {
		return new BlockCoord(this.x + vec.x, this.y + vec.y, this.z + vec.z);
	}

}
