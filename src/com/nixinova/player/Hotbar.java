package com.nixinova.player;

import com.nixinova.graphics.Render;
import com.nixinova.input.Keys;
import com.nixinova.world.Block;

public class Hotbar {
	private static int currentBlockID = 1;
	
	public static final Render[] SLOTS = new Render[] {
		/* 0 */ null,
		/* 1 */ Block.BEDROCK.getTexture(),
		/* 2 */ Block.GRASS.getTexture(),
		/* 3 */ Block.DIRT.getTexture(),
		/* 4 */ Block.STONE.getTexture(),
		/* 5 */ null,
		/* 6 */ null,
		/* 7 */ null,
		/* 8 */ null,
		/* 9 */ null,
	};
	
	public static void updateFromKbd(Keys kbd) {
		if (kbd.pressed(Keys.NUM_1))
			currentBlockID = 1;
		if (kbd.pressed(Keys.NUM_2))
			currentBlockID = 2;
		if (kbd.pressed(Keys.NUM_3))
			currentBlockID = 3;
		if (kbd.pressed(Keys.NUM_4))
			currentBlockID = 4;
		// Only 4 block slots right now
	}
	
	public static Render getCurrentBlock() {
		return SLOTS[currentBlockID];
	}

}
