package com.nixinova.mineo.ui.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.nixinova.mineo.main.Mineo;
import com.nixinova.mineo.player.input.InputHandler;
import com.nixinova.mineo.player.input.Keys;
import com.nixinova.mineo.ui.display.GameDisplay;
import com.nixinova.mineo.ui.graphics.FontGraphics;
import com.nixinova.mineo.ui.graphics.TextColorScheme;
import com.nixinova.mineo.ui.graphics.Texture;

public class MainMenu extends BaseMenu {
	private static final String BG_TEXTURE = "blocks/dirt";
	private static final double BG_SCALE = 10;

	private final FontGraphics headerFont = FontGraphics.FONT_800;
	private final FontGraphics footerFont = FontGraphics.FONT_200;

	private Keys kbd;

	private final BufferedImage bgImg;
	private final MenuButton newButton;
	private final MenuButton contButton;
	private final MenuButton exitButton;

	public MainMenu(InputHandler input) {
		super(input);
		this.kbd = input.keys;

		this.bgImg = Texture.loadScaledImage(BG_TEXTURE, BG_SCALE);
		this.contButton = new MenuButton("CONTINUE", 500, 350);
		this.newButton = new MenuButton("NEW GAME", 500, 500);
		this.exitButton = new MenuButton("EXIT", 500, 650);
	}

	public void run(Graphics g) {
		Graphics2D graphics = (Graphics2D) g;

		var buttonScheme = new TextColorScheme(Color.black, Color.lightGray, Color.black);
		var textScheme = new TextColorScheme(Color.lightGray, Color.black);

		// Clear window
		graphics.clearRect(0, 0, GameDisplay.WIDTH, GameDisplay.HEIGHT);

		// Textured background
		Rectangle2D anchorRect = new Rectangle2D.Double(0, 0, bgImg.getWidth(), bgImg.getHeight());
		graphics.setPaint(new TexturePaint(bgImg, anchorRect));
		graphics.fillRect(0, 0, GameDisplay.WIDTH, GameDisplay.HEIGHT);

		// Draw heading
		headerFont.drawStringOutlined(graphics, "MINEO JAVA", GameDisplay.WIDTH / 2 - 235, 200, textScheme);

		// Set up buttons
		contButton.setGraphics(graphics);
		newButton.setGraphics(graphics);
		exitButton.setGraphics(graphics);
		// Draw buttons
		contButton.draw(buttonScheme);
		newButton.draw(buttonScheme);
		exitButton.draw(buttonScheme);

		// Draw footer
		footerFont.drawStringOutlined(graphics, "v" + Mineo.VERSION, 10, GameDisplay.HEIGHT - 100, textScheme);
		footerFont.drawStringOutlined(graphics, String.valueOf(super.fps), 10, 10, textScheme);
		footerFont.drawStringOutlined(graphics, "© Nixinova", GameDisplay.WIDTH - 120, GameDisplay.HEIGHT - 100, textScheme);

		// Draw cursor
		graphics.setColor(Color.white);
		setFont(graphics, 20, Font.PLAIN);
		graphics.drawString("+", super.input.mouseX, super.input.mouseY);

		// Input actions
		if (kbd.pressedButton(Keys.LCLICK)) {
			if (contButton.isMouseInside(this.input)) {
				Mineo.loadSavedGame();
			}
			if (newButton.isMouseInside(this.input)) {
				Mineo.loadNewGame();
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
