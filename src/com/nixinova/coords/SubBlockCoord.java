package com.nixinova.coords;

import com.nixinova.Vector3;

public class SubBlockCoord implements ICoord<Double> {

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

	public Double value() {
		return this.x;
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
