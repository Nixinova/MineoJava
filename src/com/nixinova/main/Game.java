package com.nixinova.main;

import com.nixinova.graphics.Render3D;
import com.nixinova.input.Controller;

import java.awt.event.KeyEvent;

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
		Controller.playerX = controls.x / Render3D.TEX_SIZE;
		Controller.playerY = controls.y / Render3D.TEX_SIZE;
		Controller.playerZ = controls.z / Render3D.TEX_SIZE;
	}
}
