package com.nixinova.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.nixinova.graphics.Display;
import com.nixinova.graphics.FontGraphics;
import com.nixinova.graphics.TextColorScheme;
import com.nixinova.graphics.Texture;
import com.nixinova.input.InputHandler;
import com.nixinova.input.Keys;
import com.nixinova.main.Mineo;

public class MainMenu extends BaseMenu {
	private static final String BG_TEXTURE = "blocks/dirt";
	private static final double BG_SCALE = 10;

	private Keys kbd;

	public MainMenu(InputHandler input) {
		super(input);
		this.kbd = input.keys;
	}

	public void run(Graphics g) {
		Graphics2D graphics = (Graphics2D) g;
		FontGraphics fg = new FontGraphics(graphics);
		var buttonScheme = new TextColorScheme(Color.black, Color.lightGray, Color.black);
		var textScheme = new TextColorScheme(Color.lightGray, Color.black);

		// Set up buttons
		final MenuButton playButton = new MenuButton(graphics, fg, "    PLAY    ", 400);
		final MenuButton exitButton = new MenuButton(graphics, fg, "    EXIT    ", 600);

		// Clear
		graphics.clearRect(0, 0, Display.WIDTH, Display.HEIGHT);

		// Textured background
		BufferedImage bgImg = Texture.loadTexture(BG_TEXTURE, BG_SCALE).getBufferedImage();
		Rectangle2D anchorRect = new Rectangle2D.Double(0, 0, bgImg.getWidth(), bgImg.getHeight());
		graphics.setPaint(new TexturePaint(bgImg, anchorRect));
		graphics.fillRect(0, 0, Display.WIDTH, Display.HEIGHT);

		// Draw heading
		setFont(graphics, 100, Font.PLAIN);
		fg.load(8);
		fg.drawStringOutlined("MINEO JAVA", Display.WIDTH / 2 - 235, 200, textScheme, 8);

		// Draw buttons
		playButton.draw(buttonScheme);
		exitButton.draw(buttonScheme);

		// Draw footer
		fg.load(2);
		fg.drawStringOutlined(Mineo.VERSION, 10, Display.HEIGHT - 100, textScheme, 2);
		fg.drawStringOutlined("(c) Nixinova", Display.WIDTH - 150, Display.HEIGHT - 100, textScheme, 2);

		// Draw cursor
		graphics.setColor(Color.white);
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
