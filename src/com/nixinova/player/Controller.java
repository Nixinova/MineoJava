package com.nixinova.player;

import com.nixinova.Vector3;
import com.nixinova.blocks.Block;
import com.nixinova.blocks.HoveredBlock;
import com.nixinova.coords.BlockCoord;
import com.nixinova.coords.Coord1;
import com.nixinova.coords.Coord3;
import com.nixinova.coords.PxCoord;
import com.nixinova.coords.TxCoord;
import com.nixinova.input.InputHandler;
import com.nixinova.input.Keys;
import com.nixinova.main.Game;
import com.nixinova.options.Options;

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
		HoveredBlock lookingAt = this.game.player.getLookingAt();
		boolean isLookingAtValidBlock = lookingAt.hoveredBlock != null;
		// Block breaking
		if (kbd.pressedButton(Keys.LCLICK) && isLookingAtValidBlock && Block.isInsideWorld(lookingAt.hoveredBlock)) {
			BlockCoord hoveredBlock = lookingAt.hoveredBlock;
			this.game.world.setTextureAt(hoveredBlock, Block.AIR.getTexture());

			// Cooldown
			kbd.startButtonCooldown(Keys.LCLICK);
		}
		// Block placing
		if (kbd.pressedButton(Keys.RCLICK) && isLookingAtValidBlock && Block.isInsideWorld(lookingAt.adjacentBlock)) {
			Block block = Hotbar.getCurrentBlock();
			BlockCoord adjacentBlock = lookingAt.adjacentBlock;
			this.game.world.setTextureAt(adjacentBlock, block.getTexture());

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
			// Above ground
			if (aboveGround()) {
				// Fall due to gravity
				yMove -= Options.gravity;
				// Bounce back if put below ground
				if (belowGround())
					yMove += Options.gravity;
			}
			// Below ground
			else if (belowGround()) {
				// Shove player back to surface
				BlockCoord curBlock = this.pos.toBlock();
				PxCoord curPx = this.pos.toPx();
				int newBlockY = this.game.world.getMinGroundY(curBlock.x, curBlock.z);
				newBlockY += 1; // to put player above the block
				this.pos = Coord3.fromPx(curPx.x, Coord1.blockToPx(newBlockY), curPx.z);
			}
			// On ground
			else {
				// Allow jumping
				if (kbd.pressedKey(Keys.JUMP)) {
					this.isJumping = true;
					kbd.startKeyCooldown(Keys.JUMP);
				}
			}
		}
		// Outside the world
		else {
			// Fall when outside of world
			if (yMove == 0)
				yMove = -0.5;
		}
		// Acceleration due to gravity
		yMove *= 1 + Math.pow(1 + Options.gravity, 2);

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
			this.debugShown = !this.debugShown;
			kbd.startKeyCooldown(Keys.F3);
		}

		// differentials for controls
		PxCoord newPos2 = this.pos2.toPx();
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
		this.rot %= Math.PI * 2;
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

	private boolean aboveGround() {
		// Above the ground if the block one texel beneath the player's feet is air
		TxCoord curTx = this.pos.toTx();
		BlockCoord blockOneTxDown = Coord3.fromTx(curTx.x, curTx.y - 1, curTx.z).toBlock();
		boolean belowTxIsAir = this.game.world.isAir(blockOneTxDown);
		return belowTxIsAir;
	}

	private boolean belowGround() {
		// Below the ground if the block one texel above the player's feet is air
		TxCoord footTx = this.pos.toTx();
		BlockCoord blockOneTxUp = Coord3.fromTx(footTx.x, footTx.y + 1, footTx.z).toBlock();
		boolean inVoid = footTx.y <= 0;
		boolean aboveTxIsAir = this.game.world.isAir(blockOneTxUp);
		return inVoid || !aboveTxIsAir;
	}
}
