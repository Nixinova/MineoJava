package com.nixinova.main;

import java.util.Random;

public class Screen extends Render {
	private Render box;

	private Render3D render;

	public Screen(int width, int height) {
		super(width, height);
		Random random = new Random();
		this.render = new Render3D(width, height);
		int MAX = 256;
		this.box = new Render(MAX, MAX);

		for (int i = 0; i < MAX * MAX; i++)
			this.box.pixels[i] = random.nextInt() * random.nextInt(5) / 4;
	}

	public void render(Game game) {
		for (int i = 0; i < this.width * this.height;) {
			this.pixels[i] = 0;
			i++;
		}

		this.render.floor(game);
		draw(this.render, 0, 0);
	}
}
