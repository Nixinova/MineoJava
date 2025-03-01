package com.nixinova.menu;

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

public class MenuDisplay extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	public static int WIDTH = (int) screenSize.getWidth();
	public static int HEIGHT = (int) screenSize.getHeight();

	private Thread thread;
	private BaseMenu menu;
	private boolean running = false;

	public MenuDisplay(BaseMenu menu) {
		this.menu = menu;
		InputHandler input = this.menu.getInput();

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
		long nanosecs = 0;
		
		while (this.running) {
			long curTime = System.nanoTime();
			long passedTime = curTime - prevTime;
			prevTime = curTime;
			nanosecs += passedTime;
			
			if (nanosecs > 1e9) {
				nanosecs = 0;
				this.menu.fps = frames;
				frames = 0;
			}
			
			render();
			frames++;
		}
	}

	private void render() {
		try {
			doRender();
		} catch (IllegalStateException err) {
			// Suppress erroneous SunGraphics2D exception when changing scenes
		}
	}

	public void doRender() {
		BufferStrategy buffer = getBufferStrategy();
		if (buffer == null) {
			createBufferStrategy(2);
			return;
		}

		Graphics graphics = buffer.getDrawGraphics();

		// Draw menu
		this.menu.run(graphics);

		// Done
		graphics.dispose();
		buffer.show();
	}
}
