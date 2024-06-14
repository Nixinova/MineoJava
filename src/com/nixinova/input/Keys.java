package com.nixinova.input;

import java.awt.event.KeyEvent;

public class Keys {
	public static int ESCAPE = KeyEvent.VK_ESCAPE;
	public static int DEBUG = KeyEvent.VK_F3;

	public static int FORWARD = KeyEvent.VK_W;
	public static int BACK = KeyEvent.VK_S;
	public static int LEFT = KeyEvent.VK_A;
	public static int RIGHT = KeyEvent.VK_D;
	public static int JUMP = KeyEvent.VK_SPACE;
	public static int SPRINT = KeyEvent.VK_SHIFT;

	public static int PLACE_BLOCK = KeyEvent.VK_X;

	public static final int NUM_1 = KeyEvent.VK_1;
	public static final int NUM_2 = KeyEvent.VK_2;
	public static final int NUM_3 = KeyEvent.VK_3;
	public static final int NUM_4 = KeyEvent.VK_4;
	public static final int NUM_5 = KeyEvent.VK_5;
	public static final int NUM_6 = KeyEvent.VK_6;
	public static final int NUM_7 = KeyEvent.VK_7;
	public static final int NUM_8 = KeyEvent.VK_8;
	public static final int NUM_9 = KeyEvent.VK_9;

	private boolean[] keys;

	public Keys(boolean[] keys) {
		this.keys = keys;
	}

	public boolean pressedKey(int key) {
		return this.keys[key];
	}

	public boolean pressed(int... keyN) {
		for (int i = 0; i < keyN.length; i++) {
			if (this.pressedKey(keyN[i]))
				return true;
		}
		return false;
	}
}
