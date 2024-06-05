package com.mineo.main;

import java.util.Random;

public class Screen extends Render {
	private Render box;

	public Screen(int width, int height) {
		super(width, height);

		Random random = new Random();
		int MAX = 256;

		this.box = new Render(MAX, MAX);

		for (int i = 0; i < MAX * MAX; i++)
			this.box.pixels[i] = random.nextInt();
	}

	public void render() {
		draw(this.box, 0, 0);
	}
}
