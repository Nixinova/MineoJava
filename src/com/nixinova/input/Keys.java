package com.nixinova.input;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

// Keyboard and mouse
public class Keys {

	// Keyboard
	public static final int ESCAPE = KeyEvent.VK_ESCAPE;
	public static final int DEBUG = KeyEvent.VK_F3;

	public static final int FORWARD = KeyEvent.VK_W;
	public static final int BACK = KeyEvent.VK_S;
	public static final int LEFT = KeyEvent.VK_A;
	public static final int RIGHT = KeyEvent.VK_D;
	public static final int JUMP = KeyEvent.VK_SPACE;
	public static final int SPRINT = KeyEvent.VK_CONTROL;
	public static final int SHIFT = KeyEvent.VK_SHIFT;

	public static final int NUM_1 = KeyEvent.VK_1;
	public static final int NUM_2 = KeyEvent.VK_2;
	public static final int NUM_3 = KeyEvent.VK_3;
	public static final int NUM_4 = KeyEvent.VK_4;
	public static final int NUM_5 = KeyEvent.VK_5;
	public static final int NUM_6 = KeyEvent.VK_6;
	public static final int NUM_7 = KeyEvent.VK_7;
	public static final int NUM_8 = KeyEvent.VK_8;
	public static final int NUM_9 = KeyEvent.VK_9;

	// Mouse
	public static final int LCLICK = MouseEvent.BUTTON1;
	public static final int RCLICK = MouseEvent.BUTTON3;

	private boolean[] keys;

	public Keys(boolean[] keys) {
		this.keys = keys;
	}

	public boolean pressed(int key) {
		return this.keys[key];
	}

	public boolean pressedAny(int... keys) {
		for (int i = 0; i < keys.length; i++) {
			if (this.pressed(keys[i]))
				return true;
		}
		return false;
	}

	public boolean clickedButton(int key) {
		return this.keys[InputHandler.MOUSE_OFFSET + key];
	}
}
