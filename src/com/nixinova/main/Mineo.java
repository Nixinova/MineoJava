package com.nixinova.main;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import com.nixinova.graphics.Display;
import com.nixinova.input.InputHandler;
import com.nixinova.menu.MenuDisplay;
import com.nixinova.options.Options;

public class Mineo {
	public static final String VERSION = "0.0.21_1";
	public static final String TITLE = "Mineo " + VERSION;

	public static void main(String[] args) {
		Options.createOptions();
		BufferedImage cursor = new BufferedImage(16, 16, 2);
		Cursor blank = Toolkit.getDefaultToolkit().createCustomCursor(cursor, new Point(0, 0), "blank");

		JFrame frame = new JFrame();

		loadMainMenu(frame);

		frame.pack();
		frame.getContentPane().setCursor(blank);
		frame.setDefaultCloseOperation(3);
		frame.setSize(Display.WIDTH, Display.HEIGHT);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // windowed fullscreen
		frame.setTitle(TITLE);
		frame.setVisible(true);
		frame.setFocusable(true);
		frame.requestFocus();
	}

	public static void loadGame(JFrame frame) {
		InputHandler input = new InputHandler(frame);
		Game game = new Game(input);
		Display display = new Display(game, input);

		frame.add(display);
		frame.pack();
		display.start();
		display.requestFocusInWindow();
	}

	public static void loadMainMenu(JFrame frame) {
		InputHandler input = new InputHandler(frame);
		MenuDisplay menuDisplay = new MenuDisplay(input);

		frame.add(menuDisplay);
		frame.pack();
		menuDisplay.start();
		menuDisplay.requestFocusInWindow();
	}
}
