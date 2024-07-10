package com.nixinova.input;

import com.nixinova.Conversion;
import com.nixinova.coords.BlockCoord;
import com.nixinova.coords.Coord;
import com.nixinova.coords.PxCoord;
import com.nixinova.coords.SubBlockCoord;
import com.nixinova.main.Game;
import com.nixinova.options.Options;
import com.nixinova.player.Hotbar;
import com.nixinova.world.Block;
import com.nixinova.world.World;

public class Controller {

	public boolean debugShown = true;
	public boolean isWalking = false;

	private Game game;
	private PxCoord pos, pos2;
	private double rot, rot2;
	private double tilt, tilt2;
	private boolean isJumping = false;
	private double jumpY = 0;

	public Controller(Game game) {
		this.game = game;
		this.pos = new PxCoord();
		this.pos2 = new PxCoord();
	}

	public void tick(InputHandler input) {
		Keys kbd = input.keys;

		// Setup movement
		double xMove = 0.0D;
		double yMove = 0.0D;
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

		// Movement
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
				if (kbd.pressed(Keys.JUMP)) {
					isJumping = true;
					kbd.startCooldown(Keys.JUMP);
				}
			} else if (aboveGround()) {
				yMove -= Options.gravity;
			} else if (belowGround()) {
				this.pos.y = Conversion.pxToSubBlock(World.GROUND_Y);
				yMove = 0.001D;
			}
		} else {
			// Fall when outside of world
			if (yMove == 0)
				yMove = -0.5;
			yMove *= 1 + Math.pow(1 + Options.gravity, 2); // acceleration due to gravity
		}

		// Mouse look boundaries
		double maxTilt = 1.5;
		if (tilt < -maxTilt)
			tilt = -maxTilt;
		if (tilt > maxTilt)
			tilt = maxTilt;

		// System keys
		if (kbd.pressed(Keys.ESCAPE)) {
			System.exit(1);
		}
		if (kbd.pressed(Keys.DEBUG)) {
			debugShown = !debugShown;
			kbd.startCooldown(Keys.DEBUG);
		}

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
		this.pos2.x *= 0.3D;
		this.pos2.y *= 0.3D;
		this.pos2.z *= 0.3D;
		this.rot2 *= 0.8D;
		this.tilt2 *= 0.8D;
	}

	public Coord getPosition() {
		return Coord.fromPx(this.pos);
	}

	public double getXRot() {
		return this.rot;
	}

	public double getYRot() {
		return this.tilt;
	}

	private boolean onGround() {
		final double groundBuffer = 0.1;
		SubBlockCoord subB = this.getPosition().toSubBlock();
		int groundY = this.game.world.getGroundY((int) subB.x, (int) subB.z);
		return Math.abs(this.pos.y - Conversion.pxToSubBlock(groundY)) < groundBuffer;
	}

	private boolean aboveGround() {
		SubBlockCoord subB = this.getPosition().toSubBlock();
		return !this.onGround() && subB.y > this.game.world.getGroundY((int) subB.x, (int) subB.z);
	}

	private boolean belowGround() {
		return !this.onGround() & !this.aboveGround();
	}
}
