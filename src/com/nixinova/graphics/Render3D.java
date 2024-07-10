package com.nixinova.graphics;

import java.util.Arrays;
import java.util.stream.IntStream;

import com.nixinova.Conversion;
import com.nixinova.coords.Coord;
import com.nixinova.coords.PxCoord;
import com.nixinova.main.Game;
import com.nixinova.options.Options;
import com.nixinova.player.Player;
import com.nixinova.world.Block;
import com.nixinova.world.World;

public class Render3D extends Render {

	private double[] depthStore;
	private boolean fogAlrApplied;
	private double lastXMove, lastYMove, lastZMove, lastGround, lastRot, lastTilt;
	private boolean[] lastKbdInput;

	public Render3D(int width, int height) {
		super(width, height);
		this.depthStore = new double[width * height];
		this.fogAlrApplied = false;
		this.lastXMove = this.lastYMove = this.lastZMove = this.lastRot = this.lastTilt = 0;
	}

	public void renderWorld(Game game) {
		BlocksRenderer blockRender = new BlocksRenderer(this, game);
		blockRender.renderWorld();
		super.replaceImage(blockRender.getImage());
	}

	/** Adds depth-based fog to the pixels */
	private void renderDistLimiter(double renderDistance) {
		if (this.fogAlrApplied)
			return;

		for (int i = 0; i < imageSize(); i++) {
			// Get pixel colour
			int colour = getPixel(i);
			// Determine brightness based on depth value from Z buffer
			int brightness = (int) (renderDistance * Options.gamma * 100 / this.depthStore[i]);

			// Clamp brightness
			if (brightness < 0)
				brightness = 0;
			if (brightness > 0xFF)
				brightness = 0xFF;

			// Calculate final RGB from pixel colour + fog
			int r = colour >> 16 & 0xFF;
			int g = colour >> 8 & 0xFF;
			int b = colour & 0xFF;
			r = r * brightness / 255;
			g = g * brightness / 255;
			b = b * brightness / 255;

			// Save fog-adjusted colour to pixel
			setPixel(i, r << 16 | g << 8 | b);
			this.fogAlrApplied = true;
		}
	}
}
