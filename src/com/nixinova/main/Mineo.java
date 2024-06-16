package com.nixinova.main;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import com.nixinova.readwrite.Options;
import com.nixinova.world.World;

public class Mineo {

	public static final String VERSION = "0.0.10_3";
	public static final String TITLE = "Mineo " + VERSION;

	public static JFrame frame;
	public static World world;

	public static void main(String[] args) {
		Options.createOptions();
		BufferedImage cursor = new BufferedImage(16, 16, 2);
		Cursor blank = Toolkit.getDefaultToolkit().createCustomCursor(cursor, new Point(0, 0), "blank");

		frame = new JFrame();
		Display game = new Display();
		world = new World();

		frame.add(game);
		frame.pack();
		frame.getContentPane().setCursor(blank);
		frame.setDefaultCloseOperation(3);
		frame.setSize(Display.WIDTH, Display.HEIGHT);
		frame.setTitle(TITLE);
		frame.setVisible(true);
		frame.setFocusable(true);
		frame.requestFocus();

		game.start();
	}
}
