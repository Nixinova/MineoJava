package com.nixinova.main;

import com.nixinova.input.Controller;
import com.nixinova.types.BlockCoord;
import com.nixinova.types.Coord;
import com.nixinova.world.Blocks;

public class Game {
	public static double time = 0.0D;

	public static Controller controls;

	public Game() {
		controls = new Controller();
	}

	public static void tick(boolean[] key) {
		time += 0.0005;

		controls.tick(key);

		// Set player coords
		Coord pxCoords = new Coord(controls.x, controls.y, controls.z);
		BlockCoord blockCoords = Blocks.worldPxToBlockCoords(pxCoords);
		Mineo.world.setPlayerBlockPos(blockCoords);
		Mineo.world.setPlayerPxPos(pxCoords);
	}
}
