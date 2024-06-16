package com.nixinova.input;

import com.nixinova.main.Display;
import com.nixinova.main.Mineo;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

// Input handler: absolute mouse movement, regardless of sensitivity etc
public class InputHandler implements KeyListener, FocusListener, MouseListener, MouseMotionListener {
	public static final int MOUSE_OFFSET = 10000;

	private static final int KEYS_ARR_SIZE = 10100; // to fit both keyboard and mouse

	public boolean[] keys = new boolean[KEYS_ARR_SIZE];

	public static int mouseX, mouseY;
	public static int centerX, centerY;

	private Robot robot;
	private boolean inMotion = false;

	public InputHandler() {
		centerX = Display.WIDTH / 2;
		centerY = Display.HEIGHT / 2;

		try {
			this.robot = new Robot();
		} catch (AWTException err) {
		}
	}

	public static double mouseDX() {
		return (centerX - mouseX) / centerX;
	}

	public static double mouseDY() {
		return (centerY - mouseY) / centerY;
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		// Treat dragging the mouse the same as moving the mouse
		this.mouseMoved(event);
	}

	@Override
	public void mouseMoved(MouseEvent event) {
		// Avoid recursion as the robot mouseMove() re-triggers this function
		if (inMotion) {
			inMotion = false;
			return;
		}

		// Get frame pos info
		JFrame frame = Mineo.frame;
		Point framePos = frame.getContentPane().getLocationOnScreen();

		// Recalculate center
		centerX = (frame.getWidth() + framePos.x) / 2;
		centerY = (frame.getHeight() + framePos.y) / 2;

		// Mouse movement data
		int newX = event.getX() + framePos.x;
		int newY = event.getY() + framePos.y;
		int deltaX = newX - centerX;
		int deltaY = newY - centerY;
		mouseX += deltaX;
		mouseY += deltaY;

		// Reset cursor to center of screen to avoid going out of frame
		robot.mouseMove(centerX, centerY);
		inMotion = true;
	}

	@Override
	public void mouseClicked(MouseEvent event) {
	}

	@Override
	public void mouseEntered(MouseEvent event) {
	}

	@Override
	public void mouseExited(MouseEvent event) {
	}

	@Override
	public void mousePressed(MouseEvent event) {
		int button = event.getButton();
		this.keys[MOUSE_OFFSET + button] = true;
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		// Reset all mouse buttons
		this.keys[MOUSE_OFFSET + Keys.LCLICK] = false;
		this.keys[MOUSE_OFFSET + Keys.RCLICK] = false;
	}

	@Override
	public void focusGained(FocusEvent event) {
	}

	@Override
	public void focusLost(FocusEvent event) {
		// Clear all keypresses
		for (int i = 0; i < this.keys.length; i++)
			this.keys[i] = false;
	}

	@Override
	public void keyPressed(KeyEvent event) {
		int keyCode = event.getKeyCode();
		if (keyCode > 0 && keyCode < this.keys.length)
			this.keys[keyCode] = true;
	}

	@Override
	public void keyReleased(KeyEvent event) {
		int keyCode = event.getKeyCode();
		if (keyCode > 0 && keyCode < this.keys.length)
			this.keys[keyCode] = false;
	}

	@Override
	public void keyTyped(KeyEvent event) {
	}
}
