package com.nixinova.mineo.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import com.nixinova.mineo.maths.coords.Coord3;
import com.nixinova.mineo.maths.coords.TxCoord;
import com.nixinova.mineo.player.Player;
import com.nixinova.mineo.world.World;
import com.nixinova.mineo.world.blocks.Block;

public class SavedGame {
	public static final String SAVE_FILE = "game.dat";
	/**
	 * Semantics of version incrementing:
	 * - +1.0: Major: Fundamental file format changes that make older save files unreadable.
	 * - +0.1: Minor: Addition of new data items; keeps backward compatibility.
	 */
	public static float SAVE_VERSION = 2.0f;

	private static final String saveFilePath = Mineo.rootFolder + "/" + SAVE_FILE;

	public World world;
	public Player player;

	public SavedGame() {
		try {
			loadFromFile();
		} catch (FileNotFoundException | ArrayIndexOutOfBoundsException | Error err) {
			System.err.println("Save file not found or is corrupted.");
			this.world = new World();
			this.player = new Player(this.world.getHorizontalCentre());
			return;
		}
	}

	public static void saveToFile(Game game) {
		try {
			FileWriter writer = new FileWriter(saveFilePath);
			
			// Write version
			writer.write(String.format("v %.1f (Mineo %s)\n", SAVE_VERSION, Mineo.VERSION));

			// Writer player position
			TxCoord playerPos = game.player.getPosition().toTx();
			writer.write(String.format("%c %d,%d,%d\n", 'P', playerPos.x, playerPos.y, playerPos.z));

			// Write each changed block in world
			for (int x = 0; x < World.maxCorner.x; x++) {
				for (int y = 0; y < World.maxCorner.y; y++) {
					for (int z = 0; z < World.maxCorner.z; z++) {
						Block block = game.world.getBlockAt(x, y, z);
						int blockId = Arrays.asList(Block.BLOCKS).indexOf(block);
						
						long posData = x << 16 | y << 8 | z;
						long data = posData << 8 | blockId;
						writer.write(String.format("%x\n", data));
					}
				}
			}

			writer.close();
		} catch (IOException err) {
			System.err.println("Could not create game save file.");
		}
	}

	private void loadFromFile() throws FileNotFoundException {
		File saveFile = new File(saveFilePath);

		Scanner scanner = new Scanner(saveFile);

		Block[][][] blockChanges = new Block[World.maxCorner.x][World.maxCorner.y][World.maxCorner.z];

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			final char modeChar = line.charAt(0);

			switch (modeChar) {
				// Save file version
				case 'v' -> {
					float ver = Float.parseFloat(line.split(" ")[1]);
					if (ver != SAVE_VERSION) {
						if (Math.floor(ver) == Math.floor(SAVE_VERSION)) {
							System.err.println("Save file is out of date! Some data may not load correctly.");
						} else {
							System.err.println("Save file is critically out of date and cannot be read.");
							throw new Error();
						}
					}
				}
				// Player position data
				case 'P' -> {
					String[] posData = line.split(" ")[1].split(",");
					int posX = Integer.parseInt(posData[0]);
					int posY = Integer.parseInt(posData[1]);
					int posZ = Integer.parseInt(posData[2]);
					Coord3 pos = Coord3.fromTx(posX, posY, posZ);
					this.player = new Player(pos);
				}
				// Changed blocks data
				default -> {
					var data = Long.parseLong(line, 16);
					int x = (int) ((data >> 24) & 0xFF);
					int y = (int) ((data >> 16) & 0xFF);
					int z = (int) ((data >> 8) & 0xFF);
					int blockId = (int) (data & 0xFF);
					Block block = Block.BLOCKS[blockId];
					blockChanges[x][y][z] = block;
				}
			}
		}
		
		this.world = new World(blockChanges);

		scanner.close();
	}

}
