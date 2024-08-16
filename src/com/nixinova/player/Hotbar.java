package com.nixinova.player;

import com.nixinova.blocks.Block;
import com.nixinova.input.Keys;

public class Hotbar {
	public static final Block[] SLOTS = new Block[10];

	public static int currentBlockID = 1;

	static {
		SLOTS[1] = Block.BEDROCK;
		SLOTS[2] = Block.GRASS;
		SLOTS[3] = Block.DIRT;
		SLOTS[4] = Block.STONE;
	}

	public static void updateFromKbd(Keys kbd) {
		for (byte i = 0; i < 10; i++) {
			if (kbd.pressedKey(Keys.NUM_0 + i) && SLOTS[i] != null) {
				currentBlockID = i;
				break;
			}
		}
	}

	public static Block getCurrentBlock() {
		return SLOTS[currentBlockID];
	}

}
