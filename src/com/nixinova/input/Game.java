package com.nixinova.input;

import com.nixinova.graphics.Render3D;
import java.awt.event.KeyEvent;

public class Game {
	public static double time = 0.0D;

	public static Controller controls;

	public Game() {
		controls = new Controller();
	}

	public static void tick(boolean[] key) {
		time += 0.0005;

		// Player movement
		boolean forward = key[KeyEvent.VK_W];
		boolean back = key[KeyEvent.VK_S];
		boolean left = key[KeyEvent.VK_A];
		boolean right = key[KeyEvent.VK_D];
		boolean jump = key[KeyEvent.VK_SPACE];
		boolean sprint = key[KeyEvent.VK_SHIFT];
		boolean f3 = key[KeyEvent.VK_F3];
		boolean esc = key[KeyEvent.VK_ESCAPE];

		controls.tick(forward, back, left, right, jump, sprint, f3, esc);

		// Set player coords
		Controller.playerPxX = controls.x;
		Controller.playerPxY = controls.y;
		Controller.playerPxZ = controls.z;
		Controller.playerX = controls.x / Render3D.TEX_SIZE;
		Controller.playerY = controls.y / Render3D.TEX_SIZE;
		Controller.playerZ = controls.z / Render3D.TEX_SIZE;
	}
}
