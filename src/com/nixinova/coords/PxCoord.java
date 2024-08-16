package com.nixinova.coords;

import com.nixinova.Vector3;

public class PxCoord extends BaseCoord<Float> {

	public static class Vector extends Vector3<Float> {
		public Vector(float x, float y, float z) {
			super(x, y, z);
		}
	}

	public float x, y, z;

	public PxCoord() {
		this.x = this.y = this.z = 0.0f;
	}

	public PxCoord(double x, double y, double z) {
		this.x = (float) x;
		this.y = (float) y;
		this.z = (float) z;
	}

	public Coord3 toCoord() {
		return Coord3.fromPx(this);
	}

}
