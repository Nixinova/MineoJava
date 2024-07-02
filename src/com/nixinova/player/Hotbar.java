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

}
