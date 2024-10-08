package com.nixinova.graphics;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;

import com.nixinova.input.InputHandler;
import com.nixinova.main.Game;

public class Display extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	private static final int TPS = 60;

	private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	public static int WIDTH = (int) screenSize.getWidth();
	public static int HEIGHT = (int) screenSize.getHeight();

	private Thread thread;
	private Render3D renderer;
	private Game game;
	private boolean running = false;

	public Display(Game game, InputHandler input) {
		this.game = game;
		this.renderer = new Render3D(WIDTH, HEIGHT);

		Dimension size = new Dimension(WIDTH, HEIGHT);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);

		addKeyListener((KeyListener) input);
		addFocusListener((FocusListener) input);
		addMouseListener((MouseListener) input);
		addMouseMotionListener((MouseMotionListener) input);
	}

	public void start() {
		if (this.running)
			return;

		this.running = true;
		this.thread = new Thread(this);
		this.thread.start();
	}

	public void stop() {
		if (!this.running)
			return;

		this.running = false;

		try {
			this.thread.join();
		} catch (Exception error) {
			error.printStackTrace();
		}
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

			render();
			frames++;
		}
	}

	private void render() {
		BufferStrategy buffer = getBufferStrategy();
		if (buffer == null) {
			createBufferStrategy(2);
			return;
		}

		Graphics graphics;
		try {
			graphics = buffer.getDrawGraphics();
		} catch (NullPointerException err) {
			// Suppress erroneous SunGraphics2D exception when changing scenes
			return;
		}

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
			uiText.drawOptionsWarning();
		}

		// Done
		graphics.dispose();
		buffer.show();
	}
}
