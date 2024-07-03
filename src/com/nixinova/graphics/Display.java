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
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import com.nixinova.input.InputHandler;
import com.nixinova.main.Game;
import com.nixinova.options.Options;

public class Display extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	public static int WIDTH = (int) screenSize.getWidth();
	public static int HEIGHT = (int) screenSize.getHeight();

	private Thread thread;
	private Render3D renderer;
	private BufferedImage img;
	private Game game;
	private InputHandler input;
	private boolean running = false;
	private int[] pixels;

	public Display(Game game, InputHandler input) {
		this.input = input;
		this.game = game;

		Dimension size = new Dimension(WIDTH, HEIGHT);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);

		this.renderer = new Render3D(WIDTH, HEIGHT);
		this.img = new BufferedImage(WIDTH, HEIGHT, 1);
		this.pixels = ((DataBufferInt) this.img.getRaster().getDataBuffer()).getData();

		addKeyListener((KeyListener) this.input);
		addFocusListener((FocusListener) this.input);
		addMouseListener((MouseListener) this.input);
		addMouseMotionListener((MouseMotionListener) this.input);
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
		double unprocessedSecs = 0.0D;
		long prevTime = System.nanoTime();
		double secsPerTick = 1.0D / 60;
		int tickCount = 0;

		while (this.running) {
			long curTime = System.nanoTime();
			long passedTime = curTime - prevTime;
			prevTime = curTime;
			unprocessedSecs += passedTime / 1.0E9D;

			while (unprocessedSecs > secsPerTick) {
				tick();
				unprocessedSecs -= secsPerTick;

				tickCount++;
				if (tickCount % 60 == 0) {
					prevTime += 1000L;
					this.game.fps = frames;
					frames = 0;
				}
				if (tickCount % 600 == 0) {
					Options.createOptions();
				}
			}

			render();
			frames++;
		}
	}

	private void tick() {
		this.game.tick();
	}

	private void render() {
		BufferStrategy buffer = getBufferStrategy();
		if (buffer == null) {
			createBufferStrategy(3);
			return;
		}

		Graphics graphics = buffer.getDrawGraphics();

		// Draw world
		this.renderer.renderWorld(this.game);

		// Draw UI graphical elements
		HUD hud = new HUD(this.renderer);
		hud.drawAll();

		// Apply and render nontext elements
		for (int i = 0; i < WIDTH * HEIGHT; i++) {
			this.pixels[i] = this.renderer.pixels[i];
		}
		graphics.drawImage(this.img, 0, 0, WIDTH, HEIGHT, null);

		// Draw UI text elements
		ScreenText uiText = new ScreenText(graphics);
		if (game.controls.debugShown)
			uiText.drawMainInfo(this.game);
		uiText.drawOptionsWarning();

		// Done
		graphics.dispose();
		buffer.show();
	}
}
