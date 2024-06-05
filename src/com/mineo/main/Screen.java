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
		int i;

		for (i = 0; i < this.width * this.height;) {
			this.pixels[i] = 0;
			i++;
		}

		for (i = 0; i < 100; i++) {
			int anim = (int) (Math.sin((System.currentTimeMillis() + i) % 1000.0D / 1000.0D * Math.PI * 2.0D) * 250.0D);
			int anim2 = (int) (Math.cos((System.currentTimeMillis() + i) % 1000.0D / 1000.0D * Math.PI * 2.0D) * 50.0D);
			draw(this.box, (this.width - 256) / 2 + anim, (this.height - 256) / 2 + anim2);
		}
	}
}
