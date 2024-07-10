package com.nixinova.main;

import com.nixinova.input.Controller;
import com.nixinova.input.InputHandler;
import com.nixinova.options.Options;
import com.nixinova.player.Player;
import com.nixinova.world.World;

public class Game {
	public int fps = 0;
	public World world;
	public Player player;
	public Controller controls;
	public InputHandler input;
	public boolean[] kbdInput;

	public Game(InputHandler input) {
		this.world = new World();
		this.player = new Player();
		this.controls = new Controller(this);
		this.input = input;
		Options.createOptions();
	}

	public void tick() {
		this.kbdInput = this.input.keys.getKeyData();
		this.controls.tick(this.input);
		this.input.tick();
		// Set player coords
		this.player.position = this.controls.getPosition();
	}
}
