package com.nixinova.mineo.ui.graphics;

import java.awt.Color;
import java.awt.Graphics;

import com.nixinova.mineo.maths.PixelColor;
import com.nixinova.mineo.player.Hotbar;
import com.nixinova.mineo.ui.display.GameDisplay;
import com.nixinova.mineo.world.blocks.Block;

public class HUD {

	private Graphics graphics;

	public HUD(Graphics render) {
		this.graphics = render;
	}

	public void drawAll() {
		this.drawCursor();
		this.drawSelectedBlock();
		this.drawHotbar();
	}

	private void drawHotbar() {
		int size = 64;
		int sep = 10;
		int cell = size + sep;
		int startX = (GameDisplay.WIDTH / 2) - 4 * cell;
		int startY = GameDisplay.HEIGHT - 150;

		// Draw hotbar border
		graphics.setColor(Color.gray);
		graphics.fillRect(startX - sep, startY - sep, (9 * cell) + (sep * 2), cell + sep);
		// Draw hotbar background
		graphics.setColor(Color.black);
		graphics.fillRect(startX - sep / 2, startY - sep / 2, (9 * cell) + sep, cell);

		// Draw selected slot
		graphics.setColor(PixelColor.fromPixel(PixelColor.SELECTION_OUTLINE));
		graphics.fillRect(startX - sep / 2 + (Hotbar.currentBlockID - 1) * cell, startY - sep / 2, cell, cell);

		// Draw hotbar slots
		int curX = startX;
		int curY = startY;
		for (int i = 0; i < 10; i++) {
			Block block = Hotbar.SLOTS[i];
			if (block != null) {
				drawTextureOnScreen(block.getTexture().texture, size, curX, curY);
				curX += cell;
			}
		}
	}

	private void drawCursor() {
		int size = 5;
		int startX = (GameDisplay.WIDTH - size) / 2;
		int startY = (GameDisplay.HEIGHT - size) / 2;
		graphics.setColor(PixelColor.fromPixel(PixelColor.SELECTION_OUTLINE));
		graphics.fillRect(startX, startY, size, size);
	}

	private void drawSelectedBlock() {
		final int size = 100;
		final int padding = 30;
		int startX = GameDisplay.WIDTH - size - padding;
		int startY = padding;
		Render texture = Hotbar.getCurrentBlock().getTexture().texture;
		drawTextureOnScreen(texture, size, startX, startY);
	}

	private void drawTextureOnScreen(Render texture, int maxSize, int startX, int startY) {
		int size = maxSize / Texture.SIZE;
		for (int x = 0; x < Texture.SIZE; x++)
			for (int y = 0; y < Texture.SIZE; y++) {
				int pixel = Texture.getTexel(texture, x, y);
				graphics.setColor(PixelColor.fromPixel(pixel));
				graphics.fillRect(startX + x * size, startY + y * size, size, size);
			}
	}

}
