package com.nixinova.mineo.ui.menu;

import java.awt.Graphics;

import com.nixinova.mineo.player.input.InputHandler;
import com.nixinova.mineo.ui.display.GameDisplay;
import com.nixinova.mineo.ui.graphics.FontGraphics;
import com.nixinova.mineo.ui.graphics.TextColorScheme;

public class MenuButton {

	private final int BORDER = 5;

	public int minX, maxX;
	public int minY, maxY;

	private Graphics graphics;
	private FontGraphics fg;

	private String text;
	private int btnWidth;
	private int btnHeight;
	private int btnPadX;
	private int btnPadY;

	public MenuButton(String text, int startX, int startY) {
		btnWidth = GameDisplay.WIDTH - 2 * startX;
		btnHeight = 100;
		btnPadX = btnWidth / 2;
		btnPadY = btnHeight / 4;

		this.text = text;
		this.minX = startX;
		this.maxX = startX + btnWidth;
		this.minY = startY;
		this.maxY = startY + btnHeight;
		this.fg = FontGraphics.FONT_600;
	}

	public void setGraphics(Graphics graphics) {
		this.graphics = graphics;
	}

	public void draw(TextColorScheme colour) {
		int dedentX = fg.getTextLength(text) / 2;
		graphics.setColor(colour.border);
		graphics.fillRect(minX, minY, btnWidth, btnHeight);
		graphics.setColor(colour.fill);
		graphics.fillRect(minX + BORDER, minY + BORDER, btnWidth - 2 * BORDER, btnHeight - 2 * BORDER);

		fg.drawString(graphics, text, colour.text, minX + btnPadX - dedentX, minY + btnPadY);
	}

	public boolean isMouseInside(InputHandler input) {
		boolean xInside = input.mouseX > minX && input.mouseX < maxX;
		boolean yInside = input.mouseY > minY && input.mouseY < maxY;
		return xInside && yInside;
	}

}
