package com.nixinova.menu;

import java.awt.Graphics;

import com.nixinova.graphics.Display;
import com.nixinova.graphics.FontGraphics;
import com.nixinova.graphics.TextColorScheme;
import com.nixinova.input.InputHandler;

public class MenuButton {

	private final int btnIndent = 500;
	private final int btnBorder = 5;
	private final int btnWidth = Display.WIDTH - 2 * btnIndent;
	private final int btnHeight = 100;
	private final int btnPadX = btnWidth / 14;
	private final int btnPadY = btnHeight / 4;
	private final int btnFontSize = 6;

	public int minX, maxX;
	public int minY, maxY;

	private Graphics graphics;
	private FontGraphics fg;
	private String text;

	public MenuButton(Graphics graphics, FontGraphics fg, String text, int startY) {
		this.graphics = graphics;
		this.fg = fg;
		this.text = text;
		this.minX = btnIndent;
		this.maxX = btnIndent + btnWidth;
		this.minY = startY;
		this.maxY = startY + btnHeight;
	}

	public void draw(TextColorScheme colour) {
		graphics.setColor(colour.border);
		graphics.fillRect(btnIndent, minY, btnWidth, btnHeight);
		graphics.setColor(colour.fill);
		graphics.fillRect(btnIndent + btnBorder, minY + btnBorder, btnWidth - 2 * btnBorder, btnHeight - 2 * btnBorder);

		fg.load(btnFontSize);
		fg.drawString(text, colour.text, minX + btnPadX, minY + btnPadY);
	}

	public boolean isMouseInside(InputHandler input) {
		boolean xInside = input.mouseX > minX && input.mouseX < maxX;
		boolean yInside = input.mouseY > minY && input.mouseY < maxY;
		return xInside && yInside;
	}

}
