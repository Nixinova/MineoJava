package com.nixinova.player;

import com.nixinova.graphics.Render;
import com.nixinova.input.Keys;
import com.nixinova.world.Block;

public class Hotbar {
	public static final Render[] SLOTS = new Render[10];

	public static int currentBlockID = 1;

	static {
		SLOTS[1] = Block.BEDROCK.getTexture();
		SLOTS[2] = Block.GRASS.getTexture();
		SLOTS[3] = Block.DIRT.getTexture();
		SLOTS[4] = Block.STONE.getTexture();
	}

	public static void updateFromKbd(Keys kbd) {
		for (int i = 0; i < 10; i++) {
			if (kbd.pressed(Keys.NUM_0 + i) && SLOTS[i] != null) {
				currentBlockID = i;
				break;
			}
		}
	}

	public static Render getCurrentBlock() {
		return SLOTS[currentBlockID];
	}

	public static void drawHotbar(Render render) {
		int size = 60;
		int sep = 10;
		int cell = size + sep;
		int startX = (render.width / 2) - 4 * cell;
		int startY = render.height - 120;

		// Draw hotbar border
		render.fill(startX - sep, startY - sep, (9 * cell) + (sep * 2), cell + sep, 0x888888);
		// Draw hotbar background
		render.fill(startX - sep / 2, startY - sep / 2, (9 * cell) + sep, cell, 0x000000);

		// Draw selected slot
		render.fill(startX - sep / 2 + (currentBlockID - 1) * cell, startY - sep / 2, cell, cell, 0xEEEEAA);

		// Draw hotbar slots
		int curX = startX;
		int curY = startY;
		for (int i = 0; i < 10; i++) {
			Render texture = SLOTS[i];
			if (texture != null) {
				render.drawTextureOnScreen(texture, size, curX, curY);
				curX += cell;
			}
		}
	}

}
