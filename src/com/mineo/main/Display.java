package com.mineo.main;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.swing.JFrame;

public class Display extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 800;
	public static final int HEIGHT = 450;
	public static final String TITLE = "in-060808";

	private Thread thread;
	private Screen screen;
	private BufferedImage img;
	private Render render;
	private boolean running = false;
	private int[] pixels;

	public Display() {
		this.screen = new Screen(WIDTH, HEIGHT);
		this.img = new BufferedImage(WIDTH, HEIGHT, 1);
		this.pixels = ((DataBufferInt) this.img.getRaster().getDataBuffer()).getData();
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
		while (this.running) {
			tick();
			render();
		}
	}

	private void tick() {
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

		this.screen.render();
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
