package com.nixinova.input;

public class Controller {
	public double x;
	public double y;
	public double z;
	public double rot;
	public double x2;
	public double y2;
	public double z2;
	public double rot2;

	private int debugCooldown = 0;

	public static boolean panLeft = false;
	public static boolean panRight = false;
	public static boolean tiltUp = false;
	public static boolean tiltDown = false;
	public static boolean debugShown = true;
	public static boolean walking = false;

	public void tick(boolean forward, boolean back, boolean left, boolean right, boolean jump, boolean crouch,
			boolean sprint, boolean f3) {
		double skyHeight = 144.0D;
		double groundHeight = 16.0D;

		double rotSpeed = 0.03D;

		double moveSpeed = 1.5D;
		double sprintSpeed = 3.0D;
		double walkSpeed = 0.5D;
		double jumpHeight = 0.25D;

		double yMove = 0.0D;
		double xMove = 0.0D;
		double zMove = 0.0D;

		if (forward) {
			zMove += sprint ? sprintSpeed : moveSpeed;
			walking = true;
		}
		if (back) {
			zMove -= sprint ? sprintSpeed : moveSpeed;
			walking = true;
		}
		if (!forward && !back) {
			walking = false;
		}
		if (panLeft) {
			this.rot2 -= rotSpeed;
		}
		if (panRight) {
			this.rot2 += rotSpeed;
		}
		if (jump) {
			if (this.y <= skyHeight) {
				yMove += jumpHeight;
			} else {
				yMove *= -1.0D;
				while (this.y > skyHeight) {
					this.y -= 0.1D;
				}
			}
		}
		if (crouch) {
			if (this.y >= groundHeight) {
				yMove -= jumpHeight;
			} else {
				yMove = 0.0D;
			}
		}

		if (this.y < 0.0D) {
			this.y = 8.0D;
			yMove = 1.0D;
		}
		if (f3 && this.debugCooldown > 10) {
			debugShown = !debugShown;
			this.debugCooldown = 0;
		}
		this.debugCooldown++;
		System.out.println(this.debugCooldown);

		this.x2 += (xMove * Math.cos(this.rot) + zMove * Math.sin(this.rot)) * walkSpeed;
		this.y2 += yMove;
		this.z2 += (zMove * Math.cos(this.rot) + xMove * Math.sin(this.rot)) * walkSpeed;

		this.x += this.x2;
		this.y += this.y2;
		this.z += this.z2;

		this.x2 *= 0.1D;
		this.y *= 0.9D;
		this.z2 *= 0.1D;
		this.rot += this.rot2;
		this.rot2 *= 0.5D;
	}
}
