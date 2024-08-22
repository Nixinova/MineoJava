package com.nixinova.graphics;

import java.awt.Color;
import java.awt.Graphics;

import com.nixinova.PixelColor;
import com.nixinova.main.Game;

public class Render3D extends Render {
	private static final double SKY_SIZE = 1.5;

	private BlocksRenderer blockRender;

	public Render3D(int width, int height) {
		super(width, height);
		this.blockRender = new BlocksRenderer(width, height);
	}

	public void renderWorld(Game game, Graphics graphics) {
		// Clear window
		graphics.setColor(Color.black);
		graphics.fillRect(0, 0, Display.WIDTH, Display.HEIGHT);

		// Prepare renderer
		blockRender.prepare(game, graphics);

		// Render sky
		drawSky(game, graphics);

		// Render blocks
		blockRender.renderWorld();

		// Update screen image
		super.replaceImage(blockRender.getImage());
	}

	private void drawSky(Game game, Graphics graphics) {
		final int baseBlue = 0x0000AA;
		final int bandHeight = 10;
		final double gradientMult = 0.97;

		double tilt = game.controls.getMouseVertRads();
		double skySize = Display.HEIGHT * (SKY_SIZE + tilt) + game.player.getPosition().toBlock().y;

		// Generate starting blue colour from mouse angle
		int currentBlue = baseBlue;
		for (double i = tilt; i > 0; i -= 0.01) {
			// brighten blue as player is looking upwards
			currentBlue /= gradientMult;
		}
		for (double i = tilt; i < 0; i += 0.01) {
			// darken blue as player is looking downwards
			currentBlue *= gradientMult;
		}
		// create bands of sky colour
		for (int vert = 0; vert < skySize; vert += bandHeight) {
			currentBlue *= gradientMult;
			int gradientedBlue = currentBlue;
			if (gradientedBlue > 0xFF)
				gradientedBlue = 0xFF;
			if (gradientedBlue < 0)
				gradientedBlue = 0;

			graphics.setColor(PixelColor.fromPixel(gradientedBlue));
			graphics.fillRect(0, vert, Display.WIDTH, vert + bandHeight);
		}
	}
}
