package com.nixinova.input;

import com.nixinova.main.Display;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class InputHandler implements KeyListener, FocusListener, MouseListener, MouseMotionListener {
	public boolean[] key = new boolean[68836];

	public Robot robot;

	public static int mouseX;
	public static int mouseY;

	public void mouseDragged(MouseEvent event) {
	}

	public void mouseMoved(MouseEvent event) {
		mouseX = event.getX();
		mouseY = event.getY();

		try {
			if (mouseX < 40) {
				Robot robot = new Robot();
				robot.mouseMove(Display.WIDTH / 2, Display.HEIGHT / 2);
			}
			if (mouseX > Display.WIDTH - 40) {
				Robot robot = new Robot();
				robot.mouseMove(Display.WIDTH / 2, Display.HEIGHT / 2);
			}
		} catch (AWTException err) {
			err.printStackTrace();
		}
	}

	public void mouseClicked(MouseEvent event) {
	}

	public void mouseEntered(MouseEvent event) {
	}

	public void mouseExited(MouseEvent event) {
	}

	public void mousePressed(MouseEvent event) {
	}

	public void mouseReleased(MouseEvent event) {
	}

	public void focusGained(FocusEvent event) {
	}

	public void focusLost(FocusEvent event) {
		for (int i = 0; i < this.key.length; i++)
			this.key[i] = false;
	}

	public void keyPressed(KeyEvent event) {
		int keyCode = event.getKeyCode();
		if (keyCode > 0 && keyCode < this.key.length)
			this.key[keyCode] = true;
	}

	public void keyReleased(KeyEvent event) {
		int keyCode = event.getKeyCode();
		if (keyCode > 0 && keyCode < this.key.length)
			this.key[keyCode] = false;
	}

	public void keyTyped(KeyEvent event) {
	}
}
