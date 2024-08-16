package com.nixinova.input;

import com.nixinova.Vector3;
import com.nixinova.coords.Coord1;
import com.nixinova.coords.Coord3;
import com.nixinova.coords.PxCoord;
import com.nixinova.main.Game;
import com.nixinova.player.Player;

public class Controller {

	public boolean uiShown = true;
	public boolean gameInfoShown = false;
	public boolean isWalking = false;

	protected Coord3 pos;
	protected double rot;
	protected double tilt;

	private ControlsTick interaction;
	private Game game;

	public Controller(Game game, Player player) {
		this.game = game;
		this.interaction = new ControlsTick(this.game, this);
		this.pos = player.getPosition();
	}

	public void tick(InputHandler input) {
		interaction.tick(input);
	}

	public Coord3 getFootPosition() {
		return this.pos;
	}

	public Coord3 getCameraPosition() {
		PxCoord posPx = this.pos.toPx();
		float heightPx = Coord1.fromSubBlock(Player.PLAYER_HEIGHT).toPx();
		return Coord3.fromPx(posPx.x, posPx.y + heightPx, posPx.z);
	}

	public double getMouseHorizRads() {
		return this.rot;
	}

	public double getMouseHorizDeg() {
		return (Math.toDegrees(getMouseHorizRads()) + 360) % 360;
	}

	public double getMouseVertRads() {
		return this.tilt;
	}

	public double getMouseVertDeg() {
		return Math.toDegrees(game.controls.getMouseVertRads());
	}

	public Vector3<Float> getViewDirection() {
		float x = (float) (Math.cos(this.tilt) * Math.sin(this.rot));
		float y = (float) Math.sin(this.tilt);
		float z = (float) (Math.cos(this.rot) * Math.cos(this.tilt));

		// Normalize
		float length = (float) Math.sqrt(x * x + y * y + z * z);
		x /= length;
		y /= length;
		z /= length;

		// Return unit vector
		return new Vector3<Float>(x, y, z);
	}
}
