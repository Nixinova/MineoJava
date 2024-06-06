package com.nixinova.input;

public class Controller {
	public double x;
	public double z;
	public double x2;
	public double z2;
	public double xRot;
	public double yRot;
	public double xRot2;
	public double yRot2;

	public static boolean panLeft = false;
	public static boolean panRight = false;
	public static boolean tiltUp = false;
	public static boolean tiltDown = false;

	public void tick(boolean forward, boolean back, boolean left, boolean right) {
		double rotSpeed = 0.03D;
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
		if (panLeft)
			this.xRot2 -= rotSpeed;
		if (panRight)
			this.xRot2 += rotSpeed;
		if (tiltUp)
			this.yRot2 -= rotSpeed;
		if (tiltDown)
			this.yRot2 += rotSpeed;

		this.x2 += (xMove * Math.cos(this.xRot) + zMove * Math.sin(this.xRot)) * walkSpeed;
		this.z2 += (zMove * Math.cos(this.xRot) + xMove * Math.sin(this.xRot)) * walkSpeed;
		this.x += this.x2;
		this.z += this.z2;
		this.x2 *= 0.1D;
		this.z2 *= 0.1D;
		this.xRot += this.xRot2;
		this.xRot2 *= 0.5D;
		this.yRot += this.yRot2;
		this.yRot2 *= 0.5D;
	}
}
