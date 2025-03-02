package com.nixinova.mineo.player;

import com.nixinova.mineo.player.input.InputHandler;
import com.nixinova.mineo.player.input.Keys;
import com.nixinova.mineo.world.blocks.Block;

public class Hotbar {
	public static final Block[] SLOTS = new Block[10];

	public static int currentBlockID = 1;
	
	private static int MAX_USED_SLOT = 4;

	static {
		SLOTS[1] = Block.BEDROCK;
		SLOTS[2] = Block.GRASS;
		SLOTS[3] = Block.DIRT;
		SLOTS[4] = Block.STONE;
	}

	public static void updateFromInput(InputHandler input) {
		Keys kbd = input.keys;
		// Number key switching
		for (int i = 0; i < 10; i++) {
			if (kbd.pressedKey(Keys.NUM_0 + i) && SLOTS[i] != null) {
				currentBlockID = i;
				break;
			}
		}
		// Scroll switching
		if (input.scroll > 0) incrementBlock();
		if (input.scroll < 0) decrementBlock();
	}

	public static Block getCurrentBlock() {
		return SLOTS[currentBlockID];
	}
	
	private static void incrementBlock() {
		currentBlockID++;
		if (currentBlockID > MAX_USED_SLOT) currentBlockID = 1;
	}
	
	private static void decrementBlock() {
		currentBlockID--;
		if (currentBlockID < 1) currentBlockID = MAX_USED_SLOT;
	}

}
