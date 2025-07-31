package com.nixinova.mineo.main;

import com.nixinova.mineo.io.Options;
import com.nixinova.mineo.io.WorldSaving;
import com.nixinova.mineo.player.Player;
import com.nixinova.mineo.player.input.Controller;
import com.nixinova.mineo.player.input.InputHandler;
import com.nixinova.mineo.ui.graphics.Raycast;
import com.nixinova.mineo.world.World;
import com.nixinova.mineo.world.blocks.HoveredBlock;

public class Game {
	public int fps = 0;
	public World world;
	public Player player;
	public Controller controls;
	public InputHandler input;
	public boolean[] kbdInput;

	public Game() {
	}

	public void setInput(InputHandler input) {
		this.input = input;
	}

	public void initNew() {
		this.world = new World();
		this.player = new Player(this.world.getHorizontalCentre());
		this.controls = new Controller(this, this.player);

		Options.createOptions();
	}

	public void initSaved() {
		WorldSaving gameSave = new WorldSaving();
		this.world = gameSave.world;
		this.player = gameSave.player;
		this.controls = new Controller(this, this.player);
		this.controls.setMouseLook(gameSave.lookHoriz, gameSave.lookVert);
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
