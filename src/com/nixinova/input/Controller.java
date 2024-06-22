package com.nixinova.input;

import com.nixinova.main.Mineo;
import com.nixinova.main.Player;
import com.nixinova.readwrite.Options;
import com.nixinova.types.BlockCoord;
import com.nixinova.world.Block;
import com.nixinova.world.World;

public class Controller {

	public double x;
	public double y;
	public double z;
	public double rot;
	public double tilt;
	public int curX, curY;

	public int playerGround = 0;
	public boolean debugShown = true;
	public boolean isWalking = false;

	private double x2;
	private double y2;
	private double z2;
	private double rot2;
	private double tilt2;

	private int debugCooldown = 0;
	private double groundBuffer = 0.1D;
	private boolean isJumping = false;
	private double jumpY = 0;

	public void tick(InputHandler input, boolean[] keys) {
		Keys kbd = new Keys(keys);

		// Setup movement
		double yMove = 0.0D;
		double xMove = 0.0D;
		double zMove = 0.0D;

		// Placing
		if (kbd.clickedButton(Keys.LCLICK)) {
			BlockCoord lookingAt = Mineo.player.getLookingAt();
			if (Block.isInsideWorld(lookingAt)) {
				Mineo.world.setTextureAt(lookingAt.x, lookingAt.y, lookingAt.z, Hotbar.getCurrentBlock());
			}
		}

		// Selecting block
		Hotbar.updateFromKbd(kbd);

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
		isWalking = kbd.pressedAny(Keys.FORWARD, Keys.BACK, Keys.LEFT, Keys.RIGHT);

		/// Mouse look
		double mouseDX = Options.sensitivity * input.deltaX;
		double mouseDY = Options.sensitivity * input.deltaY;
		if (mouseDX != 0) {
			this.rot2 += mouseDX;
		}
		if (mouseDY != 0) {
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
				playerGround++;
				if (playerGround > World.GROUND_Y)
					playerGround = World.GROUND_Y;
				this.jumpY = 0;
			}
		}
		if (Mineo.player.isWithinWorld()) {
			if (onGround()) {
				if (kbd.pressed(Keys.JUMP))
					isJumping = true;
			} else if (aboveGround()) {
				yMove -= Options.jumpHeight * Options.gravity;
			} else if (belowGround()) {
				this.y = Player.PLAYER_HEIGHT + groundBuffer * 1.01D;
				yMove = 0.001D;
			}
		} else {
			// Fall when outside of world
			if (yMove == 0)
				yMove = -0.5;
			yMove *= 1 + Options.gravity; // acceleration due to gravity
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
		this.x2 *= 0.6D;
		this.y2 *= 0.3D;
		this.z2 *= 0.6D;
		this.rot2 *= 0.8D;
		this.tilt2 *= 0.8D;
	}

	public boolean onGround() {
		return Math.abs(this.y - Player.PLAYER_HEIGHT) < groundBuffer;
	}

	public boolean aboveGround() {
		return this.y > Player.PLAYER_HEIGHT;
	}

	public boolean belowGround() {
		return this.y < Player.PLAYER_HEIGHT;
	}
}
