package com.nixinova.input;

public class Controller {
	public double x;
	public double z;
	public double rot;
	public double x2;
	public double z2;
	public double rot2;

	public void tick(boolean forward, boolean back, boolean left, boolean right, boolean turnLeft, boolean turnRight) {
		double rotSpeed = 0.01D;
		double walkSpeed = 0.5D;
		double xMove = 0.0D;
		double zMove = 0.0D;

		if (forward)
			zMove++;
		if (back)
			zMove--;
		if (left)
			xMove--;
		if (right)
			xMove++;
		if (turnLeft)
			this.rot2 -= rotSpeed;
		if (turnRight)
			this.rot2 += rotSpeed;

		this.x2 += (xMove * Math.cos(this.rot) + zMove * Math.sin(this.rot)) * walkSpeed;
		this.z2 += (zMove * Math.cos(this.rot) + xMove * Math.sin(this.rot)) * walkSpeed;
		this.x += this.x2;
		this.z += this.z2;
		this.x2 *= 0.1D;
		this.z2 *= 0.1D;
		this.rot += this.rot2;
		this.rot2 *= 0.8D;
	}
}
