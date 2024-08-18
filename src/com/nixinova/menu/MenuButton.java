package com.nixinova.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import com.nixinova.graphics.Display;
import com.nixinova.input.InputHandler;

public class MenuButton {

	private final int btnIndent = 500;
	private final int btnBorder = 5;
	private final int btnWidth = Display.WIDTH - 2 * btnIndent;
	private final int btnHeight = 100;
	private final int btnTextStart = 20 + btnHeight / 2;
	private final int btnFontSize = 60;

	public int minX, maxX;
	public int minY, maxY;

	private Graphics graphics;
	private String text;

	public MenuButton(Graphics graphics, String text, int startY) {
		this.graphics = graphics;
		this.text = text;
		this.minX = btnIndent;
		this.maxX = btnIndent + btnWidth;
		this.minY = startY;
		this.maxY = startY + btnHeight;
	}

	public void draw() {
		this.graphics.setColor(Color.black);
		this.graphics.fillRect(btnIndent, this.minY, btnWidth, btnHeight);
		this.graphics.setColor(Color.gray);
		this.graphics.fillRect(btnIndent + btnBorder, this.minY + btnBorder,
			btnWidth - 2 * btnBorder, btnHeight - 2 * btnBorder);
		this.graphics.setColor(Color.black);
		this.graphics.setFont(new Font(this.graphics.getFont().getName(), Font.PLAIN, btnFontSize));
		this.graphics.drawString(this.text, getStringStartX(this.text, this.graphics), this.minY + btnTextStart);
	}

	public void drawPlain() {
		this.graphics.drawString(this.text, getStringStartX(this.text, this.graphics), this.minY + btnTextStart);
	}

	public void drawPlainOutline(int scale) {
		for (int x = -scale; x <= scale; x += scale) {
			for (int y = -scale; y <= scale; y += scale) {
				this.graphics.drawString(this.text, getStringStartX(this.text, this.graphics) + x,
					this.minY + btnTextStart + y);
			}
		}
	}

	public void drawPlainWithOutline(Color inner, Color border, int scale) {
		this.graphics.setColor(border);
		drawPlainOutline(scale);
		this.graphics.setColor(inner);
		drawPlain();
	}

	public boolean isMouseInside(InputHandler input) {
		boolean xInside = input.mouseX > this.minX && input.mouseX < this.maxX;
		boolean yInside = input.mouseY > this.minY && input.mouseY < this.maxY;
		return xInside && yInside;
	}

	private int getStringStartX(String string, Graphics graphics) {
		FontMetrics fontInfo = graphics.getFontMetrics();
		int textWidth = fontInfo.stringWidth(string);
		// int textHeight = fontInfo.getHeight();
		int x = (Display.WIDTH - textWidth) / 2;
		return x;
	}

}
