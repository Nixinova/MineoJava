package com.nixinova.graphics;

import com.nixinova.input.Game;

public class Screen extends Render {
	private Render3D render;

	public Screen(int width, int height) {
		super(width, height);
		this.render = new Render3D(width, height);
	}

	public void render(Game game) {
		for (int i = 0; i < this.width * this.height; i++)
			this.pixels[i] = 0;
		
		this.render.floor(game);
		this.render.renderDistLimiter();
		draw(this.render, 0, 0);
	}
}
