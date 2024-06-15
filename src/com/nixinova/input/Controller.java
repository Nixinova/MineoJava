package com.nixinova.input;


import com.nixinova.graphics.Render;
import com.nixinova.graphics.Textures;
import com.nixinova.main.Mineo;
import com.nixinova.readwrite.Options;
import com.nixinova.types.BlockCoord;
import com.nixinova.world.Blocks;

public class Controller {

	public static boolean debugShown = true;
	public static boolean isWalking = false;

	private boolean panLeft = false;
	private boolean panRight = false;
	private boolean tiltUp = false;
	private boolean tiltDown = false;

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

	private int currentBlockID = 1;
	private Render currentBlock = Textures.bedrock;

	public void tick(boolean[] keys) {
		checkControls();

		Keys kbd = new Keys(keys);

		// Setup movement
		double yMove = 0.0D;
		double xMove = 0.0D;
		double zMove = 0.0D;

		// Placing
		if (kbd.pressed(Keys.PLACE_BLOCK)) {
			BlockCoord lookingAt = Mineo.world.getLookingAt();
			Mineo.world.setTextureAt(lookingAt.x, lookingAt.z, currentBlock);
		}

		// Selecting block
		if (kbd.pressed(Keys.NUM_1))
			currentBlockID = 1;
		if (kbd.pressed(Keys.NUM_2))
			currentBlockID = 2;
		if (kbd.pressed(Keys.NUM_3))
			currentBlockID = 3;
		if (kbd.pressed(Keys.NUM_4))
			currentBlockID = 4;
		currentBlock = Blocks.BLOCKS[currentBlockID];

		/// Movement
		double mvChange = kbd.pressed(Keys.SPRINT) ? Options.sprintSpeed : Options.walkSpeed;
		if (kbd.pressed(Keys.FORWARD)) {
			zMove += mvChange;
		}
		if (kbd.pressed(Keys.BACK)) {
			zMove += -mvChange;
		}
		if (kbd.pressed(Keys.RIGHT)) {
			xMove += mvChange;
		}
		if (kbd.pressed(Keys.LEFT)) {
			xMove += -mvChange;
		}
		isWalking = kbd.pressed(Keys.FORWARD, Keys.BACK, Keys.LEFT, Keys.RIGHT);

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
			if (kbd.pressed(Keys.JUMP))
				isJumping = true;
		} else if (aboveGround()) {
			yMove -= Options.jumpHeight * Options.gravity;
		} else if (belowGround()) {
			this.y = Options.groundHeight + groundBuffer * 1.01D;
			yMove = 0.001D;
		}

		// Mouse look boundaries
		double maxTilt = 1.0;
		if (tilt < -maxTilt)
			tilt = -maxTilt;
		if (tilt > maxTilt)
			tilt = maxTilt;

		// System keys
		if (kbd.pressed(Keys.ESCAPE)) {
			System.exit(1);
		}
		if (kbd.pressed(Keys.DEBUG) && this.debugCooldown > 10) {
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
