package com.nixinova.mineo.maths.coords;

import com.nixinova.mineo.maths.Vector3;

public class PxCoord extends BaseCoord<Double> {

	public double x;
	public double y;
	public double z;

	public PxCoord() {
		this.x = this.y = this.z = 0.0;
	}

	public PxCoord(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Coord3 toCoord() {
		return Coord3.fromPx(this);
	}

	public void add(Vector3<Double> vec) {
		this.x += vec.x;
		this.y += vec.y;
		this.z += vec.z;
	}

	public PxCoord applyVector(Vector3<Double> vec) {
		return new PxCoord(this.x + vec.x, this.y + vec.y, this.z + vec.z);
	}

}
