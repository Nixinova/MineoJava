package com.nixinova.input;

import com.nixinova.readwrite.Options;

public class Controller {
	public double x;
	public double y;
	public double z;
	public double rot;

	private double x2;
	private double y2;
	private double z2;
	private double rot2;
	private int debugCooldown = 0;
	private double groundBuffer = 0.1D;

	public static boolean panLeft = false;
	public static boolean panRight = false;
	public static boolean tiltUp = false;
	public static boolean tiltDown = false;
	public static boolean debugShown = true;
	public static boolean walking = false;
	public static boolean escapePressed = false;

	public void tick(boolean forward, boolean back, boolean left, boolean right, boolean jump, boolean crouch,
			boolean sprint, boolean f3, boolean esc) {
		double yMove = 0.0D;
		double xMove = 0.0D;
		double zMove = 0.0D;

		if (forward) {
			zMove += sprint ? Options.sprintSpeed : Options.moveSpeed;
			walking = true;
		}
		if (back) {
			zMove -= sprint ? Options.sprintSpeed : Options.moveSpeed;
			walking = true;
		}
		if (right) {
			xMove += sprint ? Options.sprintSpeed : Options.moveSpeed;
			walking = true;
		}
		if (left) {
			xMove -= sprint ? Options.sprintSpeed : Options.moveSpeed;
			walking = true;
		}
		if (!forward && !back && !left && !right) {
			walking = false;
		}
		if (panLeft) {
			this.rot2 -= Options.rotationSpeed;
		}
		if (panRight) {
			this.rot2 += Options.rotationSpeed;
		}
		if (onGround()) {
			if (jump) {
				yMove += Options.jumpHeight;
			}
		} else {
			if (aboveGround()) {
				yMove -= Options.jumpHeight * 0.04D;
			}
			if (belowGround()) {
				this.y = Options.groundHeight + groundBuffer * 1.01D;
				yMove = 0.01D;
			}
		}

		if (esc) {
			System.exit(1);
		}
		if (f3 && this.debugCooldown > 10) {
			debugShown = !debugShown;
			this.debugCooldown = 0;
		}
		this.debugCooldown++;

		this.x2 += (xMove * Math.cos(this.rot) + zMove * Math.sin(this.rot)) * Options.walkSpeed;
		this.y2 += yMove;
		this.z2 += (zMove * Math.cos(this.rot) - xMove * Math.sin(this.rot)) * Options.walkSpeed;

		this.x += this.x2;
		this.y += this.y2;
		this.z += this.z2;

		this.x2 *= 0.1D;
		this.y2 *= 0.9D;
		this.z2 *= 0.1D;
		this.rot += this.rot2;
		this.rot2 *= 0.5D;
	}

	public boolean onGround() {
		return Math.abs(this.y - Options.groundHeight) < groundBuffer;
	}

	public boolean aboveGround() {
		return this.y > Options.groundHeight;
	}

	public boolean belowGround() {
		return this.y < Options.groundHeight;
	}
}
