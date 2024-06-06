package com.nixinova.main;

import com.nixinova.graphics.Screen;
import com.nixinova.input.InputHandler;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Calendar;
import javax.swing.JFrame;

public class Display extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 854;
	public static final int HEIGHT = 477;

	static Calendar rightNow = Calendar.getInstance();
	static String month = String.format("%02d", new Object[] { Integer.valueOf(rightNow.get(2) + 1) });
	static String day = String.format("%02d", new Object[] { Integer.valueOf(rightNow.get(5)) });
	static String hour = String.format("%02d", new Object[] { Integer.valueOf(rightNow.get(11)) });

	public static final String TITLE = "in-" + month + day + hour;

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

		addKeyListener((KeyListener) this.input);
		addFocusListener((FocusListener) this.input);
		addMouseListener((MouseListener) this.input);
		addMouseMotionListener((MouseMotionListener) this.input);
	}

	private void start() {
		this.running = true;
		this.thread = new Thread(this);
		this.thread.start();
	}

	private void stop() {
		if (this.running)
			return;

		this.running = false;

		try {
			this.thread.join();
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	public void run() {
		int frames = 0;
		double unprocessedSecs = 0.0D;
		long prevTime = System.nanoTime();
		double secsPerTick = 1.0D / 60;
		int tickCount = 0;
		boolean ticked = false;

		while (this.running) {
			long curTime = System.nanoTime();
			long passedTime = curTime - prevTime;
			prevTime = curTime;
			double billion = 1.0E9D;
			unprocessedSecs += passedTime / billion;

			while (unprocessedSecs > secsPerTick) {
				tick();
				unprocessedSecs -= secsPerTick;
				ticked = true;
				tickCount++;
				if (tickCount % 60 == 0)
					System.out.println(String.valueOf(frames) + " FPS");
				prevTime += 1000L;
				frames = 0;
			}

			if (ticked) {
				render();
				frames++;
			}

			render();
			frames++;
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

		for (int i = 0; i < WIDTH * HEIGHT; i++) {
			this.pixels[i] = this.screen.pixels[i];
		}

		this.screen.render(this.game);
		Graphics graphics = buffer.getDrawGraphics();
		graphics.drawImage(this.img, 0, 0, WIDTH, HEIGHT, null);
		graphics.dispose();

		buffer.show();
	}

	public static void main(String[] args) {
		Display game = new Display();
		JFrame frame = new JFrame();

		frame.add(game);
		frame.setDefaultCloseOperation(3);
		frame.setSize(WIDTH, HEIGHT);
		frame.setTitle(TITLE);
		frame.setLocationRelativeTo((Component) null);
		frame.setResizable(false);
		frame.setVisible(true);

		game.start();
	}
}
