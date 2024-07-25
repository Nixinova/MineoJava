package com.nixinova.graphics;

import com.nixinova.main.Game;

public class Render3D extends Render {
	private BlocksRenderer blockRender;

	public Render3D(int width, int height) {
		super(width, height);
		this.blockRender = new BlocksRenderer(width, height);
	}

	public void renderWorld(Game game) {
		// Prepare renderer
		blockRender.prepare(game);
		// Render blocks
		blockRender.renderWorld();
		super.replaceImage(blockRender.getImage());
	}
}
