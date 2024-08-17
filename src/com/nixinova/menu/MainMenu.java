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
	private static final String BG_TEXTURE = "blocks/stone";

	private Keys kbd;

	public MainMenu(InputHandler input) {
		super(input);
		this.kbd = input.keys;
	}

	public void render(Graphics graphics) {
		final String fontName = graphics.getFont().getName();

		// Clear
		graphics.clearRect(0, 0, Display.WIDTH, Display.HEIGHT);

		// Textured background
		BufferedImage grassBlockImg = Texture.loadTexture(BG_TEXTURE).getBufferedImage();
		Rectangle2D anchorRect = new Rectangle2D.Double(0, 0, grassBlockImg.getWidth(), grassBlockImg.getHeight());
		Graphics2D g2d = (Graphics2D) graphics;
		g2d.setPaint(new TexturePaint(grassBlockImg, anchorRect));
		g2d.fillRect(0, 0, Display.WIDTH, Display.HEIGHT);

		// Draw heading
		graphics.setFont(new Font(fontName, Font.BOLD, 60));
		graphics.setColor(Color.black);
		graphics.drawString("MINEO JAVA", Display.WIDTH / 2 - 200, 100);

		// Draw version
		graphics.setFont(new Font(fontName, Font.PLAIN, 20));
		graphics.drawString(Mineo.VERSION, 10, Display.HEIGHT - 80);

		// Draw cursor
		graphics.setFont(new Font(fontName, Font.PLAIN, 20));
		if (kbd.pressedButton(Keys.LCLICK))
			graphics.setColor(Color.green);
		if (kbd.pressedButton(Keys.RCLICK))
			graphics.setColor(Color.red);
		graphics.drawString("+", super.input.mouseX, super.input.mouseY);

		// Exit on escape
		if (kbd.pressedKey(Keys.ESCAPE))
			System.exit(1);

	}

}
