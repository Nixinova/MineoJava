package com.nixinova.main;

import com.nixinova.graphics.Render3D;
import com.nixinova.graphics.Screen;
import com.nixinova.input.Controller;
import com.nixinova.input.InputHandler;
import com.nixinova.readwrite.Options;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.swing.JFrame;

public class Display extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	
	private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	public static int WIDTH = (int) screenSize.getWidth();
	public static int HEIGHT = (int) screenSize.getHeight();

	public static final String VERSION = "0.0.5";
	public static final String TITLE = "Mineo " + VERSION;

	public static JFrame frame;

	private Thread thread;
	private Screen screen;
	private BufferedImage img;
	private Game game;
	private InputHandler input;
	private boolean running = false;
	private int[] pixels;
	private int newX = 0;
	private int newY = 0;
	private int oldX = 0;
	private int oldY = 0;
	public int fps = 0;

	public Display() {
		Dimension size = new Dimension(WIDTH, HEIGHT);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);

		this.screen = new Screen(WIDTH, HEIGHT);
		this.img = new BufferedImage(WIDTH, HEIGHT, 1);
		this.pixels = ((DataBufferInt) this.img.getRaster().getDataBuffer()).getData();
		this.input = new InputHandler();

		addKeyListener((KeyListener) this.input);
		addFocusListener((FocusListener) this.input);
		addMouseListener((MouseListener) this.input);
		addMouseMotionListener((MouseMotionListener) this.input);
	}

	private void start() {
		if (this.running)
			return;

		this.running = true;
		this.thread = new Thread(this);
		this.thread.start();
	}

	private void stop() {
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
					Options.CreateOptions();
				}
			}

			render();
			frames++;

			this.newX = InputHandler.mouseX;
			if (this.newX > this.oldX) {
				Controller.panRight = true;
			} else if (this.newX < this.oldX) {
				Controller.panLeft = true;
			} else {
				Controller.panRight = false;
				Controller.panLeft = false;
			}
			this.oldX = this.newX;

			this.newY = InputHandler.mouseY;
			if (this.newY > this.oldY) {
				Controller.tiltUp = true;
			} else if (this.newY < this.oldY) {
				Controller.tiltDown = true;
			} else {
				Controller.tiltUp = false;
				Controller.tiltDown = false;
			}
			this.oldX = this.newX;
		}
	}

	private void tick() {
		Game.tick(this.input.key);
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

		int sep = 15;
		String intLimit = "214748364";

		String playerX = String.format("%01d", Math.round(Render3D.playerX));
		String playerY = String.format("%01d", Math.round(Render3D.playerY));
		String playerZ = String.format("%01d", Math.round(Render3D.playerZ));

		playerX = playerX.contains(intLimit) ? "infinity" : playerX;
		playerY = playerY.contains(intLimit) ? "infinity" : playerY;
		playerZ = playerZ.contains(intLimit) ? "infinity" : playerZ;

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
			graphics.setColor(Color.white);
			graphics.drawString(TITLE, 5, sep);
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

	public static void main(String[] args) {
		Options.CreateOptions();
		BufferedImage cursor = new BufferedImage(16, 16, 2);
		Cursor blank = Toolkit.getDefaultToolkit().createCustomCursor(cursor, new Point(0, 0), "blank");

		Display game = new Display();
		JFrame frame = new JFrame();

		frame.add(game);
		frame.pack();
		frame.getContentPane().setCursor(blank);
		frame.setDefaultCloseOperation(3);
		frame.setSize(WIDTH, HEIGHT);
		frame.setTitle(TITLE);
		frame.setVisible(true);
		frame.setFocusable(true);
		frame.requestFocus();

		game.start();
	}
}
