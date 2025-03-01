package com.nixinova.mineo.ui.display;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;

import com.nixinova.mineo.player.input.InputHandler;

public abstract class DisplayBase extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	protected static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	public static int WIDTH = (int) screenSize.getWidth();
	public static int HEIGHT = (int) screenSize.getHeight();

	protected Thread thread;
	protected boolean running = false;

	public DisplayBase(InputHandler input) {
		Dimension size = new Dimension(WIDTH, HEIGHT);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);

		addKeyListener((KeyListener) input);
		addFocusListener((FocusListener) input);
		addMouseListener((MouseListener) input);
		addMouseMotionListener((MouseMotionListener) input);
	}

	protected abstract void renderDisplay(Graphics graphics);

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

	// Wrapper for renderer to catch expected errors
	protected void render() {
		try {
			doRender();
		} catch (IllegalStateException err) {
			// Suppress erroneous SunGraphics2D exception when changing scenes
		}
	}

	private void doRender() {
		BufferStrategy buffer = getBufferStrategy();
		if (buffer == null) {
			createBufferStrategy(2);
			return;
		}

		Graphics graphics = buffer.getDrawGraphics();

		// Draw
		renderDisplay(graphics);

		// Done
		graphics.dispose();
		buffer.show();
	}
}
