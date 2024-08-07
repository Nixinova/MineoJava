package com.nixinova.main;

import com.nixinova.blocks.HoveredBlock;
import com.nixinova.coords.Coord3;
import com.nixinova.graphics.Raycast;
import com.nixinova.input.InputHandler;
import com.nixinova.options.Options;
import com.nixinova.player.Controller;
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
		Coord3 startPosition = Coord3.fromSubBlock(world.minCorner.x + 0.5, World.GROUND_Y, world.minCorner.z + 0.5);
		this.player = new Player(startPosition);
		this.controls = new Controller(this, this.player);
		this.input = input;
		Options.createOptions();
	}

	public void tick() {
		this.preTick();

		this.controls.tick(this.input);
		this.input.tick();

		this.postTick();
	}

	private void preTick() {
		// Prepare inputs
		this.kbdInput = this.input.keys.getKeyData();
	}

	private void postTick() {
		// Update player position
		this.player.updatePosition(this.controls.getFootPosition());

		// Set player looking at
		HoveredBlock lookingAt = Raycast.getLookingAt(this);
		this.player.setLookingAt(lookingAt);
	}
}
