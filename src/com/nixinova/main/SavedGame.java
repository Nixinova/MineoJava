package com.nixinova.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import com.nixinova.blocks.Block;
import com.nixinova.coords.BlockCoord;
import com.nixinova.coords.Coord3;
import com.nixinova.player.Player;
import com.nixinova.world.World;

public class SavedGame {
	public static final String SAVE_FILE = "game.dat";

	private static final String saveFilePath = Mineo.rootFolder + "/" + SAVE_FILE;

	public World world;
	public Player player;

	public SavedGame() {
		loadFromFile();
	}

	public static void saveToFile( World world, Player player) {
		var pos = player.getPosition().toTx();

		try {
			FileWriter writer = new FileWriter(saveFilePath);

			writer.write("PLAYER\n");
			writer.write(String.format("%d,%d,%d\n", pos.x, pos.y, pos.z));

			writer.write("WORLD\n");
			// store each changed block in world
			for (Entry<BlockCoord, Block> entry : world.getBlockChanges().entrySet()) {
				BlockCoord block = entry.getKey();
				String tex = entry.getValue().getTextureName();
				writer.write(String.format("%d,%d,%d=%s\n", block.x, block.y, block.z, tex == null ? "0" : tex));
			}

			writer.close();
		} catch (IOException err) {
			System.err.println("Could not create game save file.");
		}
	}

	private void loadFromFile() {
		File saveFile = new File(saveFilePath);

		Scanner scanner;
		try {
			scanner = new Scanner(saveFile);
		} catch (FileNotFoundException err) {
			System.err.println("Save file not found.");
			return;
		}

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();

			// Load player data
			if (line.equals("PLAYER")) {
				// Load player position if present

				if (!scanner.hasNextLine())
					break;

				String[] posData = scanner.nextLine().trim().split(",");
				int posX = Integer.parseInt(posData[0]);
				int posY = Integer.parseInt(posData[1]);
				int posZ = Integer.parseInt(posData[2]);
				this.player = new Player(Coord3.fromTx(posX, posY, posZ));
			}
			// Load world data
			else if (line.equals("WORLD")) {
				// Load block data if present

				if (!scanner.hasNextLine())
					break;

				Map<BlockCoord, Block> blockChanges = new HashMap<>();
				while (scanner.hasNextLine()) {
					String blockData = scanner.nextLine().trim();

					String[] posData = blockData.split("=")[0].split(",");
					int posX = Integer.parseInt(posData[0]);
					int posY = Integer.parseInt(posData[1]);
					int posZ = Integer.parseInt(posData[2]);
					String texName = blockData.split("=")[1];

					BlockCoord pos = new BlockCoord(posX, posY, posZ);
					blockChanges.put(pos, texName.equals("0") ? Block.AIR : new Block(texName));
				}
				this.world = new World(blockChanges);
			}
		}
		scanner.close();
	}

}
