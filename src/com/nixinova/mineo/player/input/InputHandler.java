package com.nixinova.mineo.player.input;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;

import com.nixinova.mineo.ui.display.GameDisplay;

// Input handler: absolute mouse movement, regardless of sensitivity etc
// Make sure to register all implemented classes in base display class
public class InputHandler implements KeyListener, FocusListener, MouseListener, MouseMotionListener, MouseWheelListener {
	public int mouseX, mouseY;
	public int centerX, centerY;
	public int deltaX, deltaY;
	public int scroll;
	public Keys keys;

	private JFrame frame;
	private Robot robot;
	private boolean inMotion = false;

	public InputHandler(JFrame frame) {
		this.frame = frame;
		this.keys = new Keys();

		this.centerX = GameDisplay.WIDTH / 2;
		this.centerY = GameDisplay.HEIGHT / 2;
		this.mouseX = this.centerX;
		this.mouseY = this.centerY;
		this.deltaX = 0;
		this.deltaY = 0;
		this.scroll = 0;

		try {
			this.robot = new Robot();
		} catch (AWTException err) {
		}
	}

	public void tick() {
		this.deltaX = 0;
		this.deltaY = 0;
		this.scroll = 0;
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
		Point framePos = this.frame.getContentPane().getLocationOnScreen();

		// Recalculate center
		this.centerX = (this.frame.getWidth() + framePos.x) / 2;
		this.centerY = (this.frame.getHeight() + framePos.y) / 2;

		// Mouse movement data
		int newX = event.getX() + framePos.x;
		int newY = event.getY() + framePos.y;
		this.deltaX = newX - this.centerX;
		this.deltaY = newY - this.centerY;
		this.mouseX += this.deltaX;
		this.mouseY += this.deltaY;

		// Reset cursor to center of screen to avoid going out of frame
		robot.mouseMove(this.centerX, this.centerY);
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
		this.keys.setButton(button, true);
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		// Reset all mouse buttons
		this.keys.setButton(Keys.LCLICK, false);
		this.keys.setButton(Keys.RCLICK, false);
	}

    @Override
    public void mouseWheelMoved(MouseWheelEvent event) {
        this.scroll = event.getWheelRotation();
    }

	@Override
	public void focusGained(FocusEvent event) {
		// retick when focus obtained
		this.tick();
	}

	@Override
	public void focusLost(FocusEvent event) {
		// Clear all keypresses
		this.keys.clear();
	}

	@Override
	public void keyPressed(KeyEvent event) {
		int keyCode = event.getKeyCode();
		if (keyCode > 0 && keyCode < this.keys.size())
			this.keys.setKey(keyCode, true);
	}

	@Override
	public void keyReleased(KeyEvent event) {
		int keyCode = event.getKeyCode();
		if (keyCode > 0 && keyCode < this.keys.size()) {
			this.keys.setKey(keyCode, false);
			this.keys.startKeyCooldown(keyCode);
		}
	}

	@Override
	public void keyTyped(KeyEvent event) {
	}
}
