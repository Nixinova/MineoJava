package com.nixinova.mineo.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import com.nixinova.mineo.main.Game;
import com.nixinova.mineo.main.Mineo;
import com.nixinova.mineo.maths.coords.Coord3;
import com.nixinova.mineo.maths.coords.TxCoord;
import com.nixinova.mineo.player.Player;
import com.nixinova.mineo.world.World;
import com.nixinova.mineo.world.blocks.Block;

public class WorldSaving {
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

	public WorldSaving() {
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
					int dupeCount = 0;
					for (int z = 0; z < World.maxCorner.z; z++) {
						Block block = game.world.getBlockAt(x, y, z);
						int blockId = Arrays.asList(Block.BLOCKS).indexOf(block);

						Block nextBlock = game.world.getBlockAt(x, y, z + 1);
						if (block == nextBlock) {
							dupeCount++;
							continue;
						}

						writer.write(String.format("%d,%d,%d %d %d\n", x, y, z - dupeCount, blockId, dupeCount));
						dupeCount = 0;
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
					String[] lineData = line.split(" ");
					String[] posData = lineData[0].split(",");
					int posX = Integer.parseInt(posData[0]);
					int posY = Integer.parseInt(posData[1]);
					int posZ = Integer.parseInt(posData[2]);
					int blockId = Integer.parseInt(lineData[1]);
					int times = Integer.parseInt(lineData[2]);
					Block block = Block.BLOCKS[blockId];
					for (int i = 0; i <= times; i++) {
						blockChanges[posX][posY][posZ + i] = block;
					}
				}
			}
		}

		this.world = new World(blockChanges);

		scanner.close();
	}

}
