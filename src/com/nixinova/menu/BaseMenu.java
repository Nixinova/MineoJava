package com.nixinova.menu;

import java.awt.FontMetrics;
import java.awt.Graphics;

import com.nixinova.graphics.Display;
import com.nixinova.input.InputHandler;

public abstract class BaseMenu {

	protected InputHandler input;

	public BaseMenu(InputHandler input) {
		this.input = input;
	}

	public abstract void run(Graphics graphics);

	public InputHandler getInput() {
		return input;
	}


}
