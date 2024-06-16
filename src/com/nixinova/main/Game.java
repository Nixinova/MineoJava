package com.nixinova.main;

import com.nixinova.input.Controller;
import com.nixinova.types.BlockCoord;
import com.nixinova.types.Coord;
import com.nixinova.world.Blocks;

public class Game {
	public double time = 0.0D;

	public Controller controls;
	public boolean[] kbdInput;

	public Game() {
		this.controls = new Controller();
		this.time = 0;
	}

	public  void tick(boolean[] keys) {
		this.time += 0.0005;

		kbdInput = keys;
		controls.tick(keys);

		// Set player coords
		Coord pxCoords = new Coord(controls.x, controls.y, controls.z);
		BlockCoord blockCoords = Blocks.worldPxToBlockCoords(pxCoords);
		Mineo.world.setPlayerBlockPos(blockCoords);
		Mineo.world.setPlayerPxPos(pxCoords);
	}
}
