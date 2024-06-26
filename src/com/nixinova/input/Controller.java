package com.nixinova.input;

import com.nixinova.coords.BlockCoord;
import com.nixinova.coords.Coord;
import com.nixinova.coords.PxCoord;
import com.nixinova.main.Game;
import com.nixinova.player.Hotbar;
import com.nixinova.player.Player;
import com.nixinova.readwrite.Options;
import com.nixinova.world.Block;
import com.nixinova.world.World;

public class Controller {

	public boolean debugShown = true;
	public boolean isWalking = false;

	private Game game;
	private PxCoord pos, pos2;
	private double rot, rot2;
	private double tilt, tilt2;
	private int debugCooldown = 0;
	private double groundBuffer = 0.1D;
	private boolean isJumping = false;
	private double jumpY = 0;
	private double playerGround = 0; // sub-block

	public Controller(Game game) {
		this.game = game;
		this.pos = new PxCoord();
		this.pos2 = new PxCoord();
	}

	public void tick(InputHandler input, boolean[] keys) {
		Keys kbd = new Keys(keys);

		// Setup movement
		double yMove = 0.0D;
		double xMove = 0.0D;
		double zMove = 0.0D;

		// Placing
		if (kbd.clickedButton(Keys.LCLICK)) {
			BlockCoord lookingAt = this.game.player.getLookingAt();
			if (Block.isInsideWorld(lookingAt)) {
				this.game.world.setTextureAt(lookingAt.x, lookingAt.y, lookingAt.z, Hotbar.getCurrentBlock());
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
			yMove += Options.jumpHeight;

			// Keep track of Y-increase from jumping as this.y2 decelerates
			this.jumpY += Options.jumpHeight;

			// Once maximum height reached, stop isJumping
			if (this.jumpY >= Options.jumpHeight) {
				isJumping = false;
				playerGround++;
				if (playerGround > World.GROUND_Y)
					playerGround = World.GROUND_Y;
				this.jumpY = 0;
			}
		}
		if (kbd.pressed(Keys.SHIFT)) {
			playerGround -= 0.025;
			if (playerGround < 0)
				playerGround = 0;

		} else {
			// Snap to ground when Shift is unpressed
			playerGround = Math.floor(playerGround);
		}
		if (this.game.player.isWithinWorld()) {
			if (onGround()) {
				if (kbd.pressed(Keys.JUMP))
					isJumping = true;
			} else if (aboveGround()) {
				yMove -= Options.jumpHeight * Options.gravity;
			} else if (belowGround()) {
				this.pos.y = Player.PLAYER_HEIGHT + groundBuffer * 1.01D;
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
		this.pos2.x += (xMove * Math.cos(this.rot) + zMove * Math.sin(this.rot)) * Options.walkSpeed;
		this.pos2.y += yMove;
		this.pos2.z += (zMove * Math.cos(this.rot) - xMove * Math.sin(this.rot)) * Options.walkSpeed;

		// apply differentials
		this.pos.x += this.pos2.x;
		this.pos.y += this.pos2.y;
		this.pos.z += this.pos2.z;
		this.rot += this.rot2;
		this.tilt += this.tilt2;

		// decel/interpolate
		this.pos2.x *= 0.6D;
		this.pos2.y *= 0.3D;
		this.pos2.z *= 0.6D;
		this.rot2 *= 0.8D;
		this.tilt2 *= 0.8D;
	}

	public Coord getPosition() {
		return Coord.fromPx(this.pos);
	}

	public Coord getPlayerGround() {
		return Coord.fromSubBlock(this.playerGround);
	}

	public double getXRot() {
		return this.rot;
	}

	public double getYRot() {
		return this.tilt;
	}

	public boolean onGround() {
		return Math.abs(this.pos.y - Player.PLAYER_HEIGHT) < groundBuffer;
	}

	public boolean aboveGround() {
		return this.pos.y > Player.PLAYER_HEIGHT;
	}

	public boolean belowGround() {
		return this.pos.y < Player.PLAYER_HEIGHT;
	}
}
