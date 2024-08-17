package com.nixinova.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.nixinova.graphics.Display;
import com.nixinova.graphics.Texture;
import com.nixinova.input.InputHandler;
import com.nixinova.input.Keys;
import com.nixinova.main.Mineo;

public class MainMenu extends BaseMenu {
	private static final String BG_TEXTURE = "blocks/dirt";

	private Keys kbd;

	public MainMenu(InputHandler input) {
		super(input);
		this.kbd = input.keys;
	}

	public void run(Graphics graphics) {
		// Set up buttons
		final MenuButton menuTitle = new MenuButton(graphics, "MINEO JAVA", 150);
		final MenuButton playButton = new MenuButton(graphics, "PLAY", 400);
		final MenuButton exitButton = new MenuButton(graphics, "EXIT", 600);

		// Clear
		graphics.clearRect(0, 0, Display.WIDTH, Display.HEIGHT);

		// Textured background
		BufferedImage grassBlockImg = Texture.loadTexture(BG_TEXTURE).getBufferedImage();
		Rectangle2D anchorRect = new Rectangle2D.Double(0, 0, grassBlockImg.getWidth(), grassBlockImg.getHeight());
		Graphics2D g2d = (Graphics2D) graphics;
		g2d.setPaint(new TexturePaint(grassBlockImg, anchorRect));
		g2d.fillRect(0, 0, Display.WIDTH, Display.HEIGHT);

		// Draw heading
		setFont(graphics, 60, Font.BOLD);
		graphics.setColor(Color.black);
		menuTitle.drawPlain();

		// Draw version
		setFont(graphics, 20, Font.PLAIN);
		graphics.drawString(Mineo.VERSION, 10, Display.HEIGHT - 80);

		// Draw buttons
		playButton.draw();
		exitButton.draw();

		// Draw cursor
		setFont(graphics, 20, Font.PLAIN);
		graphics.drawString("+", super.input.mouseX, super.input.mouseY);

		// Input actions
		if (kbd.pressedButton(Keys.LCLICK)) {
			if (playButton.isMouseInside(this.input)) {
				Mineo.loadGame();
			}
			if (exitButton.isMouseInside(this.input)) {
				System.exit(1);
			}

		}

	}

	private void setFont(Graphics graphics, int size, int style) {
		graphics.setFont(new Font(graphics.getFont().getName(), style, size));
	}

}
