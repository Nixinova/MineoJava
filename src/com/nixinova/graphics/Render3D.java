package com.nixinova.graphics;

import com.nixinova.main.Game;

public class Render3D extends Render {
	private BlocksRenderer blockRender;
	private boolean fogAlrApplied;
	private double lastXMove, lastYMove, lastZMove, lastGround, lastRot, lastTilt;
	private boolean[] lastKbdInput;

	public Render3D(int width, int height) {
		super(width, height);
		this.blockRender = new BlocksRenderer(width, height);
		this.fogAlrApplied = false;
		this.lastXMove = this.lastYMove = this.lastZMove = this.lastRot = this.lastTilt = 0;
	}

	public void renderWorld(Game game) {
		// Prepare renderer
		blockRender.prepare(game);
		// Render blocks
		blockRender.renderWorld();
		super.replaceImage(blockRender.getImage());
	}
}
