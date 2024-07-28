package com.nixinova.graphics;

import java.awt.Graphics;

import com.nixinova.main.Game;

public class Render3D extends Render {
	private BlocksRenderer blockRender;

	public Render3D(int width, int height) {
		super(width, height);
		this.blockRender = new BlocksRenderer(width, height);
	}

	public void renderWorld(Game game, Graphics graphics) {
		// Prepare renderer
		blockRender.prepare(game, graphics);
		// Render blocks
		blockRender.renderWorld();
		// Update screen image
		super.replaceImage(blockRender.getImage());
	}
}
