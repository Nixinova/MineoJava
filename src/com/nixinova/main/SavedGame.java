package com.nixinova.main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;

import com.nixinova.blocks.Block;
import com.nixinova.coords.BlockCoord;
import com.nixinova.player.Player;
import com.nixinova.world.World;

public class SavedGame {
	public static final String SAVE_FILE = "game.dat";

	private static final String saveFilePath = Mineo.rootFolder + "/" + SAVE_FILE;

	public Game game;
	public World world;
	public Player player;

	public SavedGame() {
	}

	public static void saveToFile(Game game, World world, Player player) {
		var playerPos = player.getPosition().toTx();

		try {
			FileWriter writer = new FileWriter(saveFilePath);

			writer.write("PLAYER\n");
			writer.write(String.format("%d,%d,%d\n", playerPos.x, playerPos.y, playerPos.z));

			writer.write("WORLD\n");
			for (Entry<BlockCoord, Block> entry : world.getBlockChanges().entrySet()) {
				BlockCoord block = entry.getKey();
				String tex = entry.getValue().getTextureName();
				writer.write(String.format("%d,%d,%d=%s\n", block.x, block.y, block.z, tex == null ? "" : tex));
			}

			writer.close();
		} catch (IOException err) {
			System.err.println("Could not create game save file.");
		}
	}

	private void loadFromFile() {
	}

}
