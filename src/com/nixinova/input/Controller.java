package com.nixinova.input;

import com.nixinova.Vector3;
import com.nixinova.coords.BlockCoord;
import com.nixinova.coords.Coord1;
import com.nixinova.coords.Coord3;
import com.nixinova.coords.PxCoord;
import com.nixinova.main.Game;
import com.nixinova.options.Options;
import com.nixinova.player.Hotbar;
import com.nixinova.player.Player;
import com.nixinova.world.Block;

public class Controller {

	public boolean debugShown = true;
	public boolean isWalking = false;

	private Game game;
	private Coord3 pos, pos2;
	private double rot, rot2;
	private double tilt, tilt2;
	private boolean isJumping = false;
	private double jumpY = 0;

	public Controller(Game game) {
		this.game = game;
		this.pos = new Coord3();
		this.pos2 = new Coord3();
	}

	public void tick(InputHandler input) {
		Keys kbd = input.keys;

		// Setup movement
		double xMove = 0.0D;
		double yMove = 0.0D;
		double zMove = 0.0D;

		// Get looking at block
		BlockCoord lookingAt = this.game.player.getLookingAt();
		// Block breaking
		if (kbd.pressedButton(Keys.LCLICK) && lookingAt != null) {
			// Break block if within world
			if (Block.isInsideWorld(lookingAt)) {
				this.game.world.setTextureAt(lookingAt.x, lookingAt.y, lookingAt.z, Block.AIR.getTexture());
			}

			// Cooldown
			kbd.startButtonCooldown(Keys.LCLICK);
		}
		// Block placing
		if (kbd.pressedButton(Keys.RCLICK) && lookingAt != null) {
			// Place block if within world
			if (Block.isInsideWorld(lookingAt)) {
				Block block = Hotbar.getCurrentBlock();
				this.game.world.setTextureAt(lookingAt.x, lookingAt.y + 1, lookingAt.z, block.getTexture());
			}

			// Cooldown
			kbd.startButtonCooldown(Keys.RCLICK);
		}

		// Hotbar
		Hotbar.updateFromKbd(kbd);

		// Movement
		double mvChange = kbd.pressedKey(Keys.SPRINT) ? Options.sprintSpeed : Options.walkSpeed;
		if (kbd.pressedKey(Keys.FORWARD)) {
			zMove += mvChange;
		}
		if (kbd.pressedKey(Keys.BACK)) {
			zMove += -mvChange;
		}
		if (kbd.pressedKey(Keys.RIGHT)) {
			xMove += mvChange;
		}
		if (kbd.pressedKey(Keys.LEFT)) {
			xMove += -mvChange;
		}
		isWalking = kbd.pressedAnyKey(Keys.FORWARD, Keys.BACK, Keys.LEFT, Keys.RIGHT);

		// Mouse look
		double mouseDX = Options.sensitivity * input.deltaX;
		double mouseDY = Options.sensitivity * input.deltaY;
		if (mouseDX != 0) {
			this.rot2 += mouseDX;
		}
		if (mouseDY != 0) {
			this.tilt2 += -mouseDY;
		}

		// Jumping
		if (isJumping) {
			yMove += Options.jumpHeight;

			// Keep track of Y-increase from jumping as this.y2 decelerates
			this.jumpY += Options.jumpHeight;

			// Once maximum height reached, stop isJumping
			if (this.jumpY >= Options.jumpHeight) {
				isJumping = false;
				this.jumpY = 0;
			}
		}

		// Ground checks
		if (this.game.player.isWithinWorld(this.game.world)) {
			if (onGround()) {
				if (kbd.pressedKey(Keys.JUMP)) {
					isJumping = true;
					kbd.startKeyCooldown(Keys.JUMP);
				}
			} else if (aboveGround()) {
				yMove -= Options.gravity;
			} else if (belowGround()) {
				// Push player up to be on the ground
				this.pos = Coord3.fromPx(this.pos.toPx().x, getGroundY().toPx(), this.pos.toPx().z);
				yMove = 0.001D;
			}
		} else {
			// Fall when outside of world
			if (yMove == 0)
				yMove = -0.5;
			// Acceleration due to gravity
			yMove *= 1 + Math.pow(1 + Options.gravity, 2);
		}

		// Mouse look boundaries
		double maxTilt = Math.toRadians(90);
		if (tilt < -maxTilt)
			tilt = -maxTilt;
		if (tilt > maxTilt)
			tilt = maxTilt;

		// System keys
		if (kbd.pressedKey(Keys.ESCAPE)) {
			System.exit(1);
		}
		if (kbd.pressedKey(Keys.F3)) {
			debugShown = !debugShown;
			kbd.startKeyCooldown(Keys.F3);
		}

		// differentials for controls
		PxCoord newPos2 = new PxCoord();
		newPos2.x += (xMove * Math.cos(this.rot) + zMove * Math.sin(this.rot)) * Options.walkSpeed;
		newPos2.y += yMove;
		newPos2.z += (zMove * Math.cos(this.rot) - xMove * Math.sin(this.rot)) * Options.walkSpeed;
		this.pos2 = Coord3.fromPx(newPos2);

		// apply differentials
		PxCoord newPos = this.pos.toPx();
		newPos.x += newPos2.x;
		newPos.y += newPos2.y;
		newPos.z += newPos2.z;
		this.pos = Coord3.fromPx(newPos);
		this.rot += this.rot2;
		this.tilt += this.tilt2;

		// decel/interpolate
		newPos2.x *= 0.3D;
		newPos2.y *= 0.3D;
		newPos2.z *= 0.3D;
		this.pos2 = Coord3.fromPx(newPos2);
		this.rot2 *= 0.8D;
		this.tilt2 *= 0.8D;

		// Modulo'ing
		this.rot %= Math.TAU;
	}

	public Coord3 getFootPosition() {
		return this.pos;
	}

	public Coord3 getCameraPosition() {
		PxCoord posPx = this.pos.toPx();
		double heightPx = Coord1.fromSubBlock(Player.PLAYER_HEIGHT).toPx();
		return Coord3.fromPx(posPx.x, posPx.y + heightPx, posPx.z);
	}

	public double getMouseHorizRads() {
		return this.rot;
	}

	public double getMouseVertRads() {
		return this.tilt;
	}

	public Vector3 getViewDirection() {
		double x = Math.cos(this.tilt) * Math.sin(this.rot);
		double y = Math.sin(this.tilt);
		double z = Math.cos(this.rot) * Math.cos(this.tilt);

		// Normalize
		double length = Math.sqrt(x * x + y * y + z * z);
		x /= length;
		y /= length;
		z /= length;

		// Return unit vector
		return new Vector3(x, y, z);
	}

	private boolean onGround() {
		final double groundBuffer = 0.1;

		return Math.abs(this.pos.toPx().y - getGroundY().toPx()) < groundBuffer;
	}

	private boolean aboveGround() {
		return !this.onGround() && this.pos.toPx().y > getGroundY().toPx();
	}

	private boolean belowGround() {
		return !this.onGround() & !this.aboveGround();
	}

	private Coord1 getGroundY() {
		BlockCoord blockCoord = this.getFootPosition().toBlock();
		int groundY = this.game.world.getGroundY(blockCoord.x, blockCoord.z);
		return Coord1.fromSubBlock(groundY);
	}
}
