package com.nixinova.mineo.main;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JFrame;

import com.nixinova.mineo.options.Options;
import com.nixinova.mineo.player.input.InputHandler;
import com.nixinova.mineo.ui.display.GameDisplay;
import com.nixinova.mineo.ui.display.MenuDisplay;
import com.nixinova.mineo.ui.menu.MainMenu;

public class Mineo {
	public static final String DATA_FOLDER = ".mineo";
	public static final String VERSION = "0.0.21_1";
	public static final String TITLE = "Mineo " + VERSION;

	public static String rootFolder;

	private static JFrame frame;

	public static void main(String[] args) {
		setupDataFolder();

		Options.createOptions();
		BufferedImage cursor = new BufferedImage(16, 16, 2);
		Cursor blank = Toolkit.getDefaultToolkit().createCustomCursor(cursor, new Point(0, 0), "blank");

		frame = new JFrame();

		// Start at main menu
		loadMainMenu();

		frame.pack();
		frame.getContentPane().setCursor(blank);
		frame.setDefaultCloseOperation(3);
		frame.setSize(GameDisplay.WIDTH, GameDisplay.HEIGHT);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // windowed fullscreen
		frame.setTitle(TITLE);
		frame.setVisible(true);
		frame.setFocusable(true);
		frame.requestFocus();
	}

	public static void loadNewGame() {
		loadGame(true);
	}

	public static void loadSavedGame() {
		loadGame(false);
	}

	private static void loadGame(boolean anew) {
		frame.getContentPane().removeAll();

		InputHandler input = new InputHandler(frame);

		Game game = new Game();
		game.setInput(input);

		if (anew)
			game.initNew();
		else
			game.initSaved();

		GameDisplay gameDisplay = new GameDisplay(game, input);

		frame.add(gameDisplay);
		frame.pack();
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // windowed fullscreen
		gameDisplay.start();
		gameDisplay.requestFocusInWindow();
	}

	public static void loadMainMenu() {
		frame.getContentPane().removeAll();

		InputHandler input = new InputHandler(frame);
		MainMenu mainMenu = new MainMenu(input);
		MenuDisplay menuDisplay = new MenuDisplay(mainMenu);

		frame.add(menuDisplay);
		frame.pack();
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // windowed fullscreen
		menuDisplay.start();
		menuDisplay.requestFocusInWindow();
	}

	private static void setupDataFolder() {
		rootFolder = String.valueOf(System.getenv("APPDATA")) + "/" + Mineo.DATA_FOLDER;
		File dir = new File(Mineo.rootFolder);
		dir.mkdir();
	}
}
