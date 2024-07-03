package com.nixinova.input;

import com.nixinova.Conversion;
import com.nixinova.coords.BlockCoord;
import com.nixinova.coords.Coord;
import com.nixinova.coords.PxCoord;
import com.nixinova.main.Game;
import com.nixinova.options.Options;
import com.nixinova.player.Hotbar;
import com.nixinova.player.Player;
import com.nixinova.world.Block;
import com.nixinova.world.World;

public class Controller {

	public boolean debugShown = true;
	public boolean isWalking = false;

	private Game game;
	private PxCoord pos, pos2; // important: *controller* position, not player position!
	private double rot, rot2;
	private double tilt, tilt2;
	private double groundBuffer = 0.1D;
	private boolean isJumping = false;
	private double jumpY = 0;
	private double playerGround = 0; // sub-block

	public Controller(Game game) {
		this.game = game;
		this.pos = new PxCoord();
		this.pos2 = new PxCoord();
	}

	public void tick(InputHandler input) {
		Keys kbd = input.keys;

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
				playerGround += Options.jumpHeight;
				if (playerGround > World.GROUND_Y)
					playerGround = World.GROUND_Y;
				this.jumpY = 0;
			}
		}
		if (kbd.pressed(Keys.SHIFT) && onGround()) {
			playerGround -= Options.gravity / Conversion.PX_PER_BLOCK; // gravity per texel instead of per block
			if (playerGround < 0)
				playerGround = 0;

		} else {
			// Snap to ground when Shift is unpressed
			playerGround = Math.floor(playerGround);
		}
		if (this.game.player.isWithinWorld()) {
			if (onGround()) {
				if (kbd.pressed(Keys.JUMP)) {
					isJumping = true;
					kbd.startCooldown(Keys.JUMP);
				}
			} else if (aboveGround()) {
				yMove -= Options.gravity;
			} else if (belowGround()) {
				this.pos.y = Player.PLAYER_HEIGHT_PX + groundBuffer * 1.01D;
				yMove = 0.001D;
			}
		} else {
			// Fall when outside of world
			if (yMove == 0)
				yMove = -0.5;
			yMove *= 1 + Math.pow(1 + Options.gravity, 2); // acceleration due to gravity
			playerGround -= Options.gravity;
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
		this.pos2.x *= 0.8D;
		this.pos2.y *= 0.3D;
		this.pos2.z *= 0.8D;
		this.rot2 *= 0.8D;
		this.tilt2 *= 0.8D;
	}

	public Coord getControllerPosition() {
		double groundOffset = this.playerGround - (int) this.playerGround;
		return Coord.fromPx(this.pos.x, this.pos.y + groundOffset, this.pos.z);
	}

	public Coord getPositionInWorld() {
		double groundVal = this.getPlayerGround().toPx().value();
		double groundOffset = this.pos.y - Player.PLAYER_HEIGHT_PX; // Y position, minus height
		Coord pos = Coord.fromPx(this.pos.x, groundVal + groundOffset, this.pos.z);
		return pos;
	}

	public Coord getPlayerGround() {
		//double offset = Conversion.pxToSubBlock(this.pos.y - Player.PLAYER_HEIGHT_PX);
		return Coord.fromSubBlock(this.playerGround);
	}

	public double getXRot() {
		return this.rot;
	}

	public double getYRot() {
		return this.tilt;
	}

	private boolean onGround() {
		return Math.abs(this.pos.y - Player.PLAYER_HEIGHT_PX) < groundBuffer;
	}

	private boolean aboveGround() {
		return this.pos.y > Player.PLAYER_HEIGHT_PX;
	}

	private boolean belowGround() {
		return this.pos.y < Player.PLAYER_HEIGHT_PX;
	}
}
