package com.nixinova.mineo.player.input;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

// Keyboard and mouse
public class Keys {
	private static final int MOUSE_OFFSET = 10000;

	public static final int ARR_SIZE = MOUSE_OFFSET + 100; // to fit both keyboard and mouse

	// Keyboard
	public static final int ESCAPE = KeyEvent.VK_ESCAPE;
	public static final int UI_TOGGLE = KeyEvent.VK_F2;
	public static final int INFO_TOGGLE = KeyEvent.VK_F3;

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
	public static final int NUM_0 = KeyEvent.VK_0;

	// Mouse
	public static final int LCLICK = MouseEvent.BUTTON1;
	public static final int RCLICK = MouseEvent.BUTTON3;

	private static final long cooldownTimeNs = (long) 0.2e9;

	private boolean[] keys;
	private long[] cooldowns;

	public Keys(boolean[] keys) {
		this.keys = keys;
		this.cooldowns = new long[keys.length];
	}

	public Keys() {
		this.keys = new boolean[ARR_SIZE];
		this.cooldowns = new long[keys.length];
	}
	
	public void clear() {
		this.keys = new boolean[ARR_SIZE];
		this.cooldowns = new long[keys.length];
	}

	public boolean pressedKey(int key) {
		return this.pressedInput(key);
	}

	public boolean pressedAnyKey(int... keys) {
		for (int i = 0; i < keys.length; i++) {
			if (this.pressedKey(keys[i]))
				return true;
		}
		return false;
	}

	public boolean pressedButton(int button) {
		return this.pressedInput(MOUSE_OFFSET + button);
	}

	public void setKey(int key, boolean value) {
		this.keys[key] = value;
	}

	public void setButton(int button, boolean value) {
		this.setKey(MOUSE_OFFSET + button, value);
	}

	public void startKeyCooldown(int key) {
		this.startInputCooldown(key);
	}

	public void startButtonCooldown(int key) {
		this.startInputCooldown(MOUSE_OFFSET + key);
	}

	public int size() {
		return this.keys.length;
	}

	public boolean[] getKeyData() {
		return this.keys;
	}

	private void startInputCooldown(int input) {
		// reset cooldown
		this.cooldowns[input] = System.nanoTime();
	}

	private boolean pressedInput(int input) {
		// Check cooldown if applicable
		long cooldownVal = this.cooldowns[input];
		if (cooldownVal > 0 && cooldownVal > System.nanoTime() - cooldownTimeNs) {
			// Still in the cooldown window, so ignore keypress
			return false;
		}

		return this.keys[input];
	}
}
