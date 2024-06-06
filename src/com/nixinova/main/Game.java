package com.nixinova.main;

import com.nixinova.input.Controller;

public class Game {
	public static double time = 0.0D;

	public static Controller controls;

	public Game() {
		controls = new Controller();
	}

	public static void tick(boolean[] key) {
		time += 5.0E-4D;

		boolean forward = key[87];
		boolean back = key[83];
		boolean left = key[65];
		boolean right = key[68];
		boolean jump = key[32];
		boolean sprint = key[16];
		boolean crouch = key[17];
		boolean f3 = key[114];
		boolean esc = key[27];

		controls.tick(forward, back, left, right, jump, crouch, sprint, f3, esc);
	}
}
