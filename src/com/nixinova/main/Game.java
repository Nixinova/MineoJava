package com.nixinova.main;

import com.nixinova.input.Controller;
import com.nixinova.input.Coord;
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
		Controller.playerPxX = controls.x;
		Controller.playerPxY = controls.y;
		Controller.playerPxZ = controls.z;
		Coord blockCoords = Blocks.worldPxToBlockCoords((int) controls.x, (int) controls.y, (int) controls.z);
		Controller.playerX = blockCoords.x;
		Controller.playerY = blockCoords.y;
		Controller.playerZ = blockCoords.z;
	}
}
