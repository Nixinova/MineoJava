package com.nixinova;

import com.nixinova.coords.SubBlockCoord;

public class Vector3<T extends Number> {

	public static class SmallInt extends Vector3<Byte> {
		public SmallInt(int x, int y, int z) {
			super((byte) x, (byte) y, (byte) z);
		}
	}

	public T x, y, z;

	public Vector3(T x, T y, T z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static SubBlockCoord.Vector blockDistance(SubBlockCoord a, SubBlockCoord b) {
		float dx = b.x - a.x;
		float dy = b.y - a.y;
		float dz = b.z - a.z;
		return new SubBlockCoord.Vector(dx, dy, dz);
	}

	public static SubBlockCoord.Vector add(SubBlockCoord.Vector a, SubBlockCoord.Vector b) {
		return new SubBlockCoord.Vector(a.x + b.x, a.y + b.y, a.z + b.z);
	}

}
