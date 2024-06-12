package com.nixinova.input;

import com.nixinova.readwrite.Options;

public class Controller {

	public static double playerX = 0;
	public static double playerY = 0;
	public static double playerZ = 0;
	public static double playerPxX = 0;
	public static double playerPxY = 0;
	public static double playerPxZ = 0;
	public static boolean panLeft = false;
	public static boolean panRight = false;
	public static boolean tiltUp = false;
	public static boolean tiltDown = false;
	public static boolean debugShown = true;
	public static boolean walking = false;
	public static boolean escapePressed = false;
	
	public double x;
	public double y;
	public double z;
	public double rot;
	public double tilt;
	public int curX, curY;

	private double x2;
	private double y2;
	private double z2;
	private double rot2;
	private double tilt2;
	private int oldX, oldY;
	private int debugCooldown = 0;
	private double groundBuffer = 0.1D;
	private boolean isJumping = false;
	private double jumpY = 0;

	public void tick(boolean forward, boolean back, boolean left, boolean right, boolean jump,
			boolean sprint, boolean f3, boolean esc) {
		double yMove = 0.0D;
		double xMove = 0.0D;
		double zMove = 0.0D;

		// Controls
		Game.controls.checkControls();

		/// Movement
		double mvChange = sprint ? Options.sprintSpeed : Options.walkSpeed;
		if (forward) {
			zMove += mvChange;
		}
		if (back) {
			zMove += -mvChange;
		}
		if (right) {
			xMove += mvChange;
		}
		if (left) {
			xMove += -mvChange;
		}
		walking = forward || back || left || right;

		/// Mouse look
		double mouseDX = Options.rotationSpeed * (1 + InputHandler.mouseDX());
		double mouseDY = Options.rotationSpeed * (1 + InputHandler.mouseDY());
		if (panLeft) {
			this.rot2 += -mouseDX;
		}
		if (panRight) {
			this.rot2 += mouseDX;
		}
		if (tiltUp) {
			this.tilt2 += mouseDY;
		}
		if (tiltDown) {
			this.tilt2 += -mouseDY;
		}

		// Ground checks
		/// Jumping
		if (isJumping) {
			yMove += Options.jumpHeight * Options.jumpStrength;

			// Keep track of Y-increase from jumping as this.y2 decelerates
			this.jumpY += Options.jumpHeight * Options.jumpStrength;

			// Once maximum height reached, stop isJumping
			if (this.jumpY >= Options.jumpHeight) {
				isJumping = false;
				this.jumpY = 0;
			}
		}
		if (onGround()) {
			if (jump)
				isJumping = true;
		}
		else 
		if (aboveGround()) {
			yMove -= Options.jumpHeight * Options.gravity;
		}
		else
		if (belowGround()) {
			this.y = Options.groundHeight + groundBuffer * 1.01D;
			yMove = 0.001D;
		}

		// Boundaries of controls
		double maxTilt = 1;
		if (tilt < -maxTilt)
			tilt = -maxTilt;
		if (tilt > maxTilt)
			tilt = maxTilt;

		// System keys
		if (esc) {
			System.exit(1);
		}
		if (f3 && this.debugCooldown > 10) {
			debugShown = !debugShown;
			this.debugCooldown = 0;
		}
		this.debugCooldown++;

		// differentials for controls
		this.x2 += (xMove * Math.cos(this.rot) + zMove * Math.sin(this.rot)) * Options.walkSpeed;
		this.y2 += yMove;
		this.z2 += (zMove * Math.cos(this.rot) - xMove * Math.sin(this.rot)) * Options.walkSpeed;

		// apply differentials
		this.x += this.x2;
		this.y += this.y2;
		this.z += this.z2;
		this.rot += this.rot2;
		this.tilt += this.tilt2;

		// decel/interpolate
		this.x2 *= 0.1D;
		this.y2 *= 0.1D;
		this.z2 *= 0.1D;
		this.rot2 *= 0.5D;
		this.tilt2 *= 0.5D;
	}

	public void checkControls() {
		this.curX = InputHandler.mouseX;
		if (this.curX > this.oldX) {
			panRight = true;
		} else if (this.curX < this.oldX) {
			panLeft = true;
		} else {
			panRight = false;
			panLeft = false;
		}
		this.oldX = this.curX;

		this.curY = InputHandler.mouseY;
		if (this.curY > this.oldY) {
			tiltDown = true;
		} else if (this.curY < this.oldY) {
			tiltUp = true;
		} else {
			tiltUp = false;
			tiltDown = false;
		}
		this.oldY = this.curY;
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
