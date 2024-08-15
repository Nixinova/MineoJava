package com.nixinova.input;

import com.nixinova.Vector3;
import com.nixinova.blocks.Block;
import com.nixinova.blocks.HoveredBlock;
import com.nixinova.coords.BlockCoord;
import com.nixinova.coords.Coord1;
import com.nixinova.coords.Coord3;
import com.nixinova.coords.PxCoord;
import com.nixinova.coords.TxCoord;
import com.nixinova.main.Game;
import com.nixinova.options.Options;
import com.nixinova.player.Hitbox;
import com.nixinova.player.Hitbox.Corner;
import com.nixinova.player.Hitbox.CornersList;
import com.nixinova.player.Hotbar;

public class ControlsTick {

	private class PositionChange {
		public Coord3 pos;
		public Coord3 dpos;
	}

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
		boolean aboveGround = isAboveGround();
		boolean belowGround = isBelowGround();
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
			// Above ground
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
		PositionChange move = resultFromMove(xMove, yMove, zMove);
		makeMove(move);
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
		controls.isWalking = kbd.pressedAnyKey(Keys.FORWARD, Keys.BACK, Keys.LEFT, Keys.RIGHT);
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
		// Shove the player if if movement takes them inside a block
		Coord3 nextMove = resultFromMove(xMove, yMove, zMove).pos;
		CornersList collisionPoints = Hitbox.getCollisionPoints(this.game.world, nextMove);
		if (collisionPoints.list.size() > 0) {
			Vector3<Double> shove = new Vector3<Double>(0d, 0d, 0d);
			// Along all planes at foot
			if (collisionPoints.containsAll(Corner.FOOT_xz, Corner.FOOT_xZ, Corner.FOOT_Xz, Corner.FOOT_XZ))
				shove.y = Options.gravity;
			// Along negative X plane at foot
			else if (collisionPoints.containsAll(Corner.FOOT_xz, Corner.FOOT_xZ))
				shove.x = mvChange;
			// Along positive X plane at foot
			else if (collisionPoints.containsAll(Corner.FOOT_Xz, Corner.FOOT_XZ))
				shove.x = -mvChange;
			// Along negative Z plane at foot
			else if (collisionPoints.containsAll(Corner.FOOT_xz, Corner.FOOT_Xz))
				shove.z = mvChange;
			// Along positive Z plane at foot
			else if (collisionPoints.containsAll(Corner.FOOT_xZ, Corner.FOOT_XZ))
				shove.z = -mvChange;
			// Shove player in the given direction
			PxCoord curPos = controls.pos.toPx();
			controls.pos = Coord3.fromPx(curPos.x + shove.x, curPos.y + shove.y, curPos.z + shove.z);
		}

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

		// Update and decelerate rotation
		controls.rot += this.drot;
		controls.tilt += this.dtilt;
		this.drot *= 0.8D;
		this.dtilt *= 0.8D;
		controls.rot %= Math.PI * 2; // modulo to be 0..360

		// Update position
		PositionChange move = resultFromMove(xMove, yMove, zMove);
		makeMove(move);
	}

	private PositionChange resultFromMove(double xMove, double yMove, double zMove) {
		PositionChange result = new PositionChange();
		result.pos = controls.pos;
		result.dpos = this.dpos;

		// differentials for controls
		PxCoord curdpos = result.dpos.toPx();
		double ddposX = (xMove * Math.cos(controls.rot) + zMove * Math.sin(controls.rot)) * Options.walkSpeed;
		double ddposY = yMove;
		double ddposZ = (zMove * Math.cos(controls.rot) - xMove * Math.sin(controls.rot)) * Options.walkSpeed;
		PxCoord newdpos = new PxCoord(curdpos.x + ddposX, curdpos.y + ddposY, curdpos.z + ddposZ);

		// apply differentials
		PxCoord curpos = result.pos.toPx();
		PxCoord newpos = new PxCoord(curpos.x + newdpos.x, curpos.y + newdpos.y, curpos.z + newdpos.z);

		// update and decelerate position
		result.pos = Coord3.fromPx(newpos);
		result.dpos = Coord3.fromPx(newdpos);
		newdpos.x *= 0.3D;
		newdpos.y *= 0.3D;
		newdpos.z *= 0.3D;
		result.dpos = Coord3.fromPx(newdpos);

		return result;
	}

	private void makeMove(PositionChange move) {
		controls.pos = move.pos;
		this.dpos = move.dpos;
	}

	// TODO check player hitbox
	private boolean isAboveGround() {
		// Above the ground if the block one texel beneath the player's feet is air
		TxCoord curTx = controls.pos.toTx();
		BlockCoord blockOneTxDown = Coord3.fromTx(curTx.x, curTx.y - 1, curTx.z).toBlock();
		boolean belowTxIsNotVoid = this.game.world.isWithinWorld(blockOneTxDown);
		boolean belowTxIsAir = this.game.world.isAir(blockOneTxDown);
		return belowTxIsNotVoid && belowTxIsAir;
	}

	// TODO check player hitbox
	private boolean isBelowGround() {
		// Below the ground if the block one texel above the player's feet is air
		TxCoord footTx = controls.pos.toTx();
		BlockCoord blockOneTxUp = Coord3.fromTx(footTx.x, footTx.y + 1, footTx.z).toBlock();
		boolean aboveTxIsAir = this.game.world.isAir(blockOneTxUp);
		return !aboveTxIsAir;
	}

}
