package com.nixinova.mineo.ui.display;

import java.awt.Graphics;

import com.nixinova.mineo.main.Game;
import com.nixinova.mineo.player.input.InputHandler;
import com.nixinova.mineo.ui.graphics.HUD;
import com.nixinova.mineo.ui.graphics.Render3D;
import com.nixinova.mineo.ui.graphics.ScreenText;

public class GameDisplay extends DisplayBase {
	private static final long serialVersionUID = 1L;

	private static final int TPS = 60;

	private Render3D renderer;
	private Game game;

	public GameDisplay(Game game, InputHandler input) {
		super(input);

		this.renderer = new Render3D(WIDTH, HEIGHT);
		this.game = game;
	}

	@Override
	public void run() {
		int frames = 0;
		long prevTime = System.nanoTime();
		double secsPerTick = 1.0 / TPS;
		double unprocessedSecs = 0;
		long nanosecs = 0;

		while (this.running) {
			long curTime = System.nanoTime();
			long passedTime = curTime - prevTime;
			prevTime = curTime;
			nanosecs += passedTime;
			unprocessedSecs += passedTime / 1e9;

			while (unprocessedSecs > secsPerTick) {
				this.game.tick();
				unprocessedSecs -= secsPerTick;

				if (nanosecs > 1e9) {
					this.game.fps = frames;
					frames = 0;
					nanosecs = 0;
				}
			}

			super.render();
			frames++;
		}
	}

	@Override
	protected void renderDisplay(Graphics graphics) {
		// Draw world
		this.renderer.renderWorld(this.game, graphics);

		// Draw UI if shown
		if (this.game.controls.uiShown) {
			// HUD
			HUD hud = new HUD(graphics);
			hud.drawAll();

			// Game info
			ScreenText uiText = new ScreenText(graphics);
			uiText.drawMainInfo(this.game);
		}
	}
}
