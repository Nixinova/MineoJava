package com.nixinova.main;

import com.nixinova.graphics.Screen;
import com.nixinova.input.Controller;
import com.nixinova.input.InputHandler;
import com.nixinova.readwrite.Options;
import com.nixinova.types.BlockCoord;

import java.awt.Canvas;
import java.awt.Color;
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

public class Display extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	public static int WIDTH = (int) screenSize.getWidth();
	public static int HEIGHT = (int) screenSize.getHeight();

	public int fps = 0;

	private Thread thread;
	private Screen screen;
	private BufferedImage img;
	private Game game;
	private InputHandler input;
	private boolean running = false;
	private int[] pixels;

	public Display() {
		Dimension size = new Dimension(WIDTH, HEIGHT);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);

		this.screen = new Screen(WIDTH, HEIGHT);
		this.img = new BufferedImage(WIDTH, HEIGHT, 1);
		this.pixels = ((DataBufferInt) this.img.getRaster().getDataBuffer()).getData();
		this.input = new InputHandler();
		this.game = new Game(this.input);

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
					this.fps = frames;
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
		this.game.tick(this.input.keys);
	}

	private void render() {
		BufferStrategy buffer = getBufferStrategy();
		if (buffer == null) {
			createBufferStrategy(3);
			return;
		}
		this.screen.render(this.game);

		for (int i = 0; i < WIDTH * HEIGHT; i++) {
			this.pixels[i] = this.screen.pixels[i];
		}

		Graphics graphics = buffer.getDrawGraphics();
		graphics.drawImage(this.img, 0, 0, WIDTH, HEIGHT, null);
		graphics.setColor(Color.white);

		BlockCoord playerBlockPos = Mineo.world.getPlayerBlockPos();
		String playerX = String.format("%d", playerBlockPos.x);
		String playerY = String.format("%d", playerBlockPos.y);
		String playerZ = String.format("%d", playerBlockPos.z);

		String msg1 = "", msg2 = "", msg3 = "";
		final int OPTIONS_VERSION = Options.OPTIONS_VERSION;
		if (Options.fileVersion < OPTIONS_VERSION || Options.fileVersion > OPTIONS_VERSION) {
			if (Options.fileVersion < OPTIONS_VERSION) {
				msg1 = "Outdated options version: client is on version " + OPTIONS_VERSION +
						" while options.txt is still on version " + Options.fileVersion + ".";
			} else if (Options.fileVersion > OPTIONS_VERSION) {
				msg1 = "Outdated client version: options.txt is on v" + Options.fileVersion +
						" while client is still on v" + OPTIONS_VERSION + ".";
			}
			msg2 = "Data in options.txt which differs from the current version may break or crash your game!";
			msg3 = "Delete options.txt in %appdata%\\.mineo to refresh the options file.";
		}

		if (Controller.debugShown) {
			final int sep = 15;
			graphics.setColor(Color.white);
			graphics.drawString(Mineo.TITLE, 5, sep);
			graphics.drawString("FPS: " + String.valueOf(this.fps), 5, sep * 2);
			graphics.drawString("Block: " + playerX + " / " + playerY + " / " + playerZ, 5, sep * 3);

			graphics.setColor(Color.red);
			graphics.drawString(msg1, 5, sep * 4);
			graphics.drawString(msg2, 5, sep * 5);
			graphics.drawString(msg3, 5, sep * 6);
		}

		graphics.dispose();
		buffer.show();
	}
}
