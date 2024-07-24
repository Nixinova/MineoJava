package com.nixinova.graphics;

import com.nixinova.blocks.Block;
import com.nixinova.player.Hotbar;

public class HUD {

	private Render render;

	public HUD(Render render) {
		this.render = render;
	}

	public void drawAll() {
		this.drawCursor();
		this.drawSelectedBlock();
		this.drawHotbar();
	}

	private void drawHotbar() {
		int size = 60;
		int sep = 10;
		int cell = size + sep;
		int startX = (render.width / 2) - 4 * cell;
		int startY = render.height - 150;

		// Draw hotbar border
		render.fill(startX - sep, startY - sep, (9 * cell) + (sep * 2), cell + sep, 0x888888);
		// Draw hotbar background
		render.fill(startX - sep / 2, startY - sep / 2, (9 * cell) + sep, cell, 0x000000);

		// Draw selected slot
		render.fill(startX - sep / 2 + (Hotbar.currentBlockID - 1) * cell, startY - sep / 2, cell, cell, 0xEEEEAA);

		// Draw hotbar slots
		int curX = startX;
		int curY = startY;
		for (int i = 0; i < 10; i++) {
			Block block = Hotbar.SLOTS[i];
			if (block != null) {
				render.drawTextureOnScreen(block.getTexture(), size, curX, curY);
				curX += cell;
			}
		}
	}

	private void drawCursor() {
		int size = 5;
		int startX = (render.width - size) / 2;
		int startY = (render.height - size) / 2;
		render.fill(startX, startY, size, size, 0xEEEEEE);
	}

	private void drawSelectedBlock() {
		final int size = 100;
		int startX = render.width - size - 50;
		int startY = 30;
		Render texture = Hotbar.getCurrentBlock().getTexture();
		render.drawTextureOnScreen(texture, size, startX, startY);
	}

}
