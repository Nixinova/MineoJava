package com.nixinova.main;

import com.nixinova.input.Controller;
import com.nixinova.input.InputHandler;
import com.nixinova.types.BlockCoord;
import com.nixinova.types.Coord;
import com.nixinova.world.Blocks;

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

		// Set player coords
		Coord pxCoords = new Coord(controls.x, controls.y, controls.z);
		BlockCoord blockCoords = Blocks.worldPxToBlockCoords(pxCoords);
		Mineo.world.setPlayerBlockPos(blockCoords);
		Mineo.world.setPlayerPxPos(pxCoords);
	}
}
