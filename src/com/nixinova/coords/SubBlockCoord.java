package com.nixinova.coords;

public class SubBlockCoord {

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

	public BlockCoord toBlockCoord() {
		return new BlockCoord((int) this.x, (int) this.y, (int) this.z);
	}
	
	public double value() {
		return this.x;
	}

}
