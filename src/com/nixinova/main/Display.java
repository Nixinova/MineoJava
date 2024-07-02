package com.nixinova.main;

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

import com.nixinova.coords.BlockCoord;
import com.nixinova.graphics.Screen;
import com.nixinova.input.InputHandler;
import com.nixinova.readwrite.Options;

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

	public Display(Game game, InputHandler input) {
		this.input = input;
		this.game = game;

		Dimension size = new Dimension(WIDTH, HEIGHT);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);

		this.screen = new Screen(WIDTH, HEIGHT);
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
		this.game.tick();
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

		BlockCoord playerBlockPos = this.game.player.position.toBlock();
		String playerX = String.format("%d", playerBlockPos.x);
		String playerY = String.format("%d", playerBlockPos.y);
		String playerZ = String.format("%d", playerBlockPos.z);

		String msg1 = "", msg2 = "", msg3 = "";
		float fileV = Options.fileVersion;
		float curV = Options.OPTIONS_VERSION;
		boolean diffMajor = (int) fileV != (int) curV;
		boolean diffMinor = (int) (fileV * 10) != (int) (curV * 10);
		if (diffMinor) { // only breaking change if major or minor is different
			if (fileV < curV)
				msg1 = "Outdated options version! Client is on " + curV + " while options.txt is on " + fileV + ".";
			else
				msg1 = "Options file too new! options.txt is on " + fileV + " while client is on " + curV + ".";

			if (diffMajor) // breaking changes to the file format
				msg2 = "Data in options.txt which differs from the current version may break or crash your game!";
			else if (diffMinor) // changes to implementation of values
				msg2 = "Data in options.txt which differs from the current version may not work correctly!";

			msg3 = "Delete options.txt in %appdata%\\.mineo and restart the game to refresh the options file.";
		}

		if (game.controls.debugShown) {
			final int sep = 15;
			graphics.setColor(Color.white);
			graphics.drawString(Mineo.TITLE, 5, sep);
			graphics.drawString("FPS: " + String.valueOf(this.fps), 5, sep * 2);
			graphics.drawString("Block: " + playerX + " / " + playerY + " / " + playerZ, 5, sep * 3);

			graphics.setColor(diffMajor ? Color.red : diffMinor ? Color.yellow : Color.white);
			graphics.drawString(msg1, 5, HEIGHT - sep * 8);
			graphics.drawString(msg2, 5, HEIGHT - sep * 7);
			graphics.drawString(msg3, 5, HEIGHT - sep * 6);
		}

		graphics.dispose();
		buffer.show();
	}
}
