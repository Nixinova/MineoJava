package com.nixinova.mineo.ui.display;

import java.awt.Graphics;

import com.nixinova.mineo.ui.menu.BaseMenu;

public class MenuDisplay extends DisplayBase {
	private static final long serialVersionUID = 1L;
	
	private BaseMenu menu;

	public MenuDisplay(BaseMenu menu) {
		super(menu.getInput());

		this.menu = menu;
	}

	@Override
	public void run() {
		int frames = 0;
		long prevTime = System.nanoTime();
		long nanosecs = 0;
		
		while (this.running) {
			long curTime = System.nanoTime();
			long passedTime = curTime - prevTime;
			prevTime = curTime;
			nanosecs += passedTime;
			
			if (nanosecs > 1e9) {
				nanosecs = 0;
				this.menu.fps = frames;
				frames = 0;
			}
			
			super.render();
			frames++;
		}
	}

	@Override
	protected void renderDisplay(Graphics graphics) {
		// Draw menu
		this.menu.run(graphics);
	}
}
