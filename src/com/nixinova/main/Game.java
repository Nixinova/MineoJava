package com.nixinova.main;

import com.nixinova.input.Controller;
import com.nixinova.input.InputHandler;
import com.nixinova.types.BlockCoord;
import com.nixinova.types.Conversion;
import com.nixinova.types.Coord;

public class Game {
	public double time = 0.0D;

	public Controller controls;
	public InputHandler input;
	public boolean[] kbdInput;

	public Game(InputHandler input) {
		this.controls = new Controller();
		this.input = input;
		this.time = 0;
	}

	public  void tick(boolean[] keys) {
		this.time += 0.0005;

		kbdInput = keys;
		controls.tick(this.input, keys);
		this.input.tick();

		// Set player coords
		Coord pxCoords = controls.getPxPositionCoords();
		BlockCoord blockCoords = Conversion.worldPxToBlockCoords(pxCoords);
		Mineo.player.setPlayerBlockPos(blockCoords);
		Mineo.player.setPlayerPxPos(pxCoords);
	}
}
