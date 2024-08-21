package com.nixinova.menu;

import java.awt.Graphics;

import com.nixinova.input.InputHandler;

public abstract class BaseMenu {
	
	public int fps;

	protected InputHandler input;

	public BaseMenu(InputHandler input) {
		this.input = input;
	}

	public abstract void run(Graphics graphics);

	public InputHandler getInput() {
		return input;
	}


}
