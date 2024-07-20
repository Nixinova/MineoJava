package com.nixinova.coords;

public class PxCoord implements ICoord<Double> {

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
	
	public Double value() {
		return this.x;
	}

}
