package com.nixinova;

import com.nixinova.coords.SubBlockCoord;

public class Vector3<T extends Number> {

	public T x, y, z;

	public Vector3(T x, T y, T z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static Vector3<Double> blockDistance(SubBlockCoord a, SubBlockCoord b) {
		double dx = a.x - b.x;
		double dy = a.y - b.y;
		double dz = a.z - b.z;
		return new Vector3<Double>(dx, dy, dz);
	}

	public static Vector3<Double> add(Vector3<Double> a, Vector3<Double> b) {
		return new Vector3<Double>(a.x + b.x, a.y + b.y, a.z + b.z);
	}

}
