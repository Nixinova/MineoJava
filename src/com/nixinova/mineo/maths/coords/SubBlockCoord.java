package com.nixinova.mineo.maths.coords;

import com.nixinova.mineo.maths.Vector3;

public class SubBlockCoord extends BaseCoord<Double> {

	public double x;
	public double y;
	public double z;

	public SubBlockCoord() {
		this.x = this.y = this.z = 0.0;
	}

	public SubBlockCoord(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Coord3 toCoord() {
		return Coord3.fromSubBlock(this);
	}

	public void add(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}

	public void add(Vector3<Double> vec) {
		this.x += vec.x;
		this.y += vec.y;
		this.z += vec.z;
	}

	public SubBlockCoord applyVector(Vector3<Double> vec) {
		return new SubBlockCoord(this.x + vec.x, this.y + vec.y, this.z + vec.z);
	}

}
