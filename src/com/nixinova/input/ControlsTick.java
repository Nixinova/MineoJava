package com.nixinova.input;

import com.nixinova.blocks.Block;
import com.nixinova.blocks.HoveredBlock;
import com.nixinova.coords.BlockCoord;
import com.nixinova.coords.Coord1;
import com.nixinova.coords.Coord3;
import com.nixinova.coords.PxCoord;
import com.nixinova.coords.SubBlockCoord;
import com.nixinova.coords.TxCoord;
import com.nixinova.main.Game;
import com.nixinova.options.Options;
import com.nixinova.player.Hotbar;
import com.nixinova.player.Player;

public class ControlsTick {

	private Game game;
	private Controller controls;

	private Coord3 dpos;
	private double drot;
	private double dtilt;

	private boolean isJumping = false;
	private double jumpY = 0;

	public ControlsTick(Game game, Controller controls) {
		this.game = game;
		this.controls = controls;
	}

	public void tick(InputHandler input) {
		// Prepare
		this.dpos = new Coord3();

		// Tick
		boolean aboveGround = aboveGround();
		boolean belowGround = belowGround();
		boolean onGround = this.game.player.isWithinWorld(this.game.world) && !aboveGround && !belowGround;
		// Natural events like gravity, etc
		tickUninputted(aboveGround, belowGround);
		// Player-controlled movement
		tickInputted(input, onGround);
	}

	private void tickUninputted(boolean aboveGround, boolean belowGround) {

		// Setup movement
		double xMove = 0.0D;
		double yMove = 0.0D;
		double zMove = 0.0D;

		// Ground checks
		if (this.game.player.isWithinWorld(this.game.world)) {
			// Inside a block
			if (aboveGround) {
				// Fall due to gravity
				yMove -= Options.gravity;
			}
			// Below ground
			else if (belowGround) {
				// Shove player back to surface
				BlockCoord curBlock = controls.pos.toBlock();
				PxCoord curPx = controls.pos.toPx();
				int newBlockY = this.game.world.getMinGroundY(curBlock.x, curBlock.z);
				newBlockY += 1; // to put player above the block
				controls.pos = Coord3.fromPx(curPx.x, Coord1.blockToPx(newBlockY), curPx.z);
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

		// Update position
		updatePos(xMove, yMove, zMove);
	}

	private void tickInputted(InputHandler input, boolean onGround) {
		Keys kbd = input.keys;

		// Setup movement
		double xMove = 0.0D;
		double yMove = 0.0D;
		double zMove = 0.0D;

		// Block actions
		HoveredBlock lookingAt = this.game.player.getLookingAt();
		boolean isLookingAtBlock = lookingAt.hoveredBlock != null;
		if (isLookingAtBlock) {
			// Block breaking
			if (kbd.pressedButton(Keys.LCLICK) && this.game.world.isWithinWorld(lookingAt.hoveredBlock)) {
				this.game.world.setTextureAt(lookingAt.hoveredBlock, Block.AIR.getTexture());

				// Cooldown
				kbd.startButtonCooldown(Keys.LCLICK);
			}

			// Block placing
			if (kbd.pressedButton(Keys.RCLICK) && this.game.world.isWithinWorld(lookingAt.adjacentBlock)) {
				Block selectedBlock = Hotbar.getCurrentBlock();
				this.game.world.setTextureAt(lookingAt.adjacentBlock, selectedBlock.getTexture());

				// Cooldown
				kbd.startButtonCooldown(Keys.RCLICK);
			}
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
		controls.isWalking = kbd.pressedAnyKey(Keys.FORWARD, Keys.BACK, Keys.LEFT, Keys.RIGHT);

		// Mouse look
		double mouseDX = Options.sensitivity * input.deltaX;
		double mouseDY = Options.sensitivity * input.deltaY;
		if (mouseDX != 0) {
			this.drot += mouseDX;
		}
		if (mouseDY != 0) {
			this.dtilt += -mouseDY;
		}

		// Jumping
		if (this.isJumping) {
			yMove += Options.jumpHeight;

			// Keep track of Y-increase from jumping as this.y2 decelerates
			this.jumpY += Options.jumpHeight;

			// Once maximum height reached, stop isJumping
			if (this.jumpY >= Options.jumpHeight) {
				this.isJumping = false;
				this.jumpY = 0;
			}
		}
		// Allow jumping if on ground
		if (onGround) {
			if (kbd.pressedKey(Keys.JUMP)) {
				this.isJumping = true;
				kbd.startKeyCooldown(Keys.JUMP);
			}
		}

		// Mouse look boundaries
		double maxTilt = Math.toRadians(90);
		if (controls.tilt < -maxTilt)
			controls.tilt = -maxTilt;
		if (controls.tilt > maxTilt)
			controls.tilt = maxTilt;

		// System keys
		if (kbd.pressedKey(Keys.ESCAPE)) {
			System.exit(1);
		}
		if (kbd.pressedKey(Keys.F3)) {
			controls.debugShown = !controls.debugShown;
			kbd.startKeyCooldown(Keys.F3);
		}

		// Update position
		updatePos(xMove, yMove, zMove);
	}

	private void updatePos(double xMove, double yMove, double zMove) {
		// differentials for controls
		PxCoord curdpos = this.dpos.toPx();
		double ddposX = (xMove * Math.cos(controls.rot) + zMove * Math.sin(controls.rot)) * Options.walkSpeed;
		double ddposY = yMove;
		double ddposZ = (zMove * Math.cos(controls.rot) - xMove * Math.sin(controls.rot)) * Options.walkSpeed;
		PxCoord newdpos = new PxCoord(curdpos.x + ddposX, curdpos.y + ddposY, curdpos.z + ddposZ);

		// apply differentials
		PxCoord curpos = controls.pos.toPx();
		PxCoord newpos = new PxCoord(curpos.x + newdpos.x, curpos.y + newdpos.y, curpos.z + newdpos.z);

		// rotation: update and decelerate
		controls.rot += this.drot;
		controls.tilt += this.dtilt;
		this.drot *= 0.8D;
		this.dtilt *= 0.8D;
		controls.rot %= Math.PI * 2; // modulo to be 0..360

		// position: update and decelerate
		controls.pos = Coord3.fromPx(newpos);
		this.dpos = Coord3.fromPx(newdpos);
		newdpos.x *= 0.3D;
		newdpos.y *= 0.3D;
		newdpos.z *= 0.3D;
		this.dpos = Coord3.fromPx(newdpos);
	}

	private boolean insideBlock(PxCoord position) {
		SubBlockCoord blockPos = Coord3.fromPx(position).toSubBlock();
		int[][] playerCornerOffsets = {
			// X, Y, Z
			{ -1, -0, -1 },
			{ +1, -0, -1 },
			{ -1, +1, -1 },
			{ -1, -0, +1 },
			{ +1, +1, -1 },
			{ +1, -0, +1 },
			{ -1, +1, +1 },
			{ +1, +1, +1 },
		};
		// check each corner of the player's hitbox for being inside a block at the new position
		for (int[] corner : playerCornerOffsets) {
			int posX = Coord1.fromSubBlock(blockPos.x + Player.PLAYER_RADIUS * corner[0]).toBlock();
			int posY = Coord1.fromSubBlock(blockPos.y + Player.PLAYER_HEIGHT * corner[1]).toBlock();
			int posZ = Coord1.fromSubBlock(blockPos.z + Player.PLAYER_RADIUS * corner[2]).toBlock();

			// if player ends up inside a block then return
			if (!this.game.world.isAir(posX, posY, posZ))
				return true;
		}
		// if no block collision found then player is not inside a block
		return false;
	}

	// TODO check player hitbox
	private boolean aboveGround() {
		// Above the ground if the block one texel beneath the player's feet is air
		TxCoord curTx = controls.pos.toTx();
		BlockCoord blockOneTxDown = Coord3.fromTx(curTx.x, curTx.y - 1, curTx.z).toBlock();
		boolean belowTxIsNotVoid = this.game.world.isWithinWorld(blockOneTxDown);
		boolean belowTxIsAir = this.game.world.isAir(blockOneTxDown);
		return belowTxIsNotVoid && belowTxIsAir;
	}

	// TODO check player hitbox
	private boolean belowGround() {
		// Below the ground if the block one texel above the player's feet is air
		TxCoord footTx = controls.pos.toTx();
		BlockCoord blockOneTxUp = Coord3.fromTx(footTx.x, footTx.y + 1, footTx.z).toBlock();
		boolean aboveTxIsAir = this.game.world.isAir(blockOneTxUp);
		return !aboveTxIsAir;
	}

}
