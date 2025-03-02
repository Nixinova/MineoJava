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
	public static float SAVE_VERSION = 1.0f;

	private static final String saveFilePath = Mineo.rootFolder + "/" + SAVE_FILE;
	private static final char CHAR_VERSION = 'v';
	private static final char CHAR_PLAYERPOS = 'P';
	private static final char CHAR_BLOCKPOS = 'B';

	public World world;
	public Player player;

	public SavedGame() {
		try {
			loadFromFile();
		} catch (FileNotFoundException | ArrayIndexOutOfBoundsException err) {
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
			writer.write(String.format("%c %.1f : Mineo %s\n", CHAR_VERSION, SAVE_VERSION, Mineo.VERSION));

			// Writer player position
			TxCoord playerPos = game.player.getPosition().toTx();
			writer.write(String.format("%c %d,%d,%d\n", CHAR_PLAYERPOS, playerPos.x, playerPos.y, playerPos.z));

			// Write each changed block in world
			for (int x = 0; x < World.maxCorner.x; x++) {
				for (int y = 0; y < World.maxCorner.y; y++) {
					for (int z = 0; z < World.maxCorner.z; z++) {
						Block block = game.world.getBlockAt(x, y, z);
						int blockId = Arrays.asList(Block.BLOCKS).indexOf(block);
						writer.write(String.format("%c %d,%d,%d %s\n", CHAR_BLOCKPOS, x, y, z, blockId));
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
			String[] lineParts = line.split(" ");
			final char modeChar = lineParts[0].charAt(0);

			switch (modeChar) {
				// Save file version
				case CHAR_VERSION -> {
					float ver = Float.parseFloat(lineParts[1]);
					if (ver != SAVE_VERSION) {
						System.err.println("Save file is out of date!");
					}
				}
				// Player position data
				case CHAR_PLAYERPOS -> {
					String[] posData = lineParts[1].trim().split(",");
					int posX = Integer.parseInt(posData[0]);
					int posY = Integer.parseInt(posData[1]);
					int posZ = Integer.parseInt(posData[2]);
					Coord3 pos = Coord3.fromTx(posX, posY, posZ);
					this.player = new Player(pos);
				}
				// Changed blocks data
				case CHAR_BLOCKPOS -> {
					String[] posData = lineParts[1].trim().split(",");
					int blockId = Integer.parseInt(lineParts[2]);
					int posX = Integer.parseInt(posData[0]);
					int posY = Integer.parseInt(posData[1]);
					int posZ = Integer.parseInt(posData[2]);
					Block block = Block.BLOCKS[blockId];
					blockChanges[posX][posY][posZ] = block;
				}
			}
		}
		
		this.world = new World(blockChanges);
		
		scanner.close();
	}

}
