package com.nixinova.coords;

import com.nixinova.Vector3;

public class SubBlockCoord extends BaseCoord<Float> {

	public static class Vector extends Vector3<Float> {
		public Vector(float x, float y, float z) {
			super(x, y, z);
		}
	}

	public float x, y, z;

	public SubBlockCoord() {
		this.x = this.y = this.z = 0.0f;
	}

	public SubBlockCoord(double x, double y, double z) {
		this.x = (float) x;
		this.y = (float) y;
		this.z = (float) z;
	}

	public Coord3 toCoord() {
		return Coord3.fromSubBlock(this);
	}

	public SubBlockCoord applyVector(SubBlockCoord.Vector vec) {
		return new SubBlockCoord(this.x + vec.x, this.y + vec.y, this.z + vec.z);
	}

}
