package com.nixinova.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import com.nixinova.blocks.Block;
import com.nixinova.coords.BlockCoord;
import com.nixinova.coords.Coord3;
import com.nixinova.coords.TxCoord;
import com.nixinova.player.Player;
import com.nixinova.world.World;

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
		loadFromFile();
	}

	public static void saveToFile(Game game) {
		try {
			FileWriter writer = new FileWriter(saveFilePath);
			
			// Write version
			writer.write(String.format("%c %.1f\n", CHAR_VERSION, SAVE_VERSION));

			// Writer player position
			writer.write(String.format("%c %d\n", CHAR_PLAYERPOS, packCoord(game.player.getPosition())));

			// Write each changed block in world
			for (Entry<BlockCoord, Block> entry : game.world.getBlockChanges().entrySet()) {
				BlockCoord block = entry.getKey();
				int packPos = packCoord(Coord3.fromBlock(block));
				int blockId = Arrays.asList(Block.BLOCKS).indexOf(entry.getValue());
				writer.write(String.format("%c %d %s\n", CHAR_BLOCKPOS, packPos, blockId));
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

		Map<BlockCoord, Block> blockChanges = new HashMap<>();

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
					int packedPos = Integer.parseInt(lineParts[1]);
					this.player = new Player(unpackCoord(packedPos));
				}
				// Changed blocks data
				case CHAR_BLOCKPOS -> {
					int packedPos = Integer.parseInt(lineParts[1]);
					int blockId = Integer.parseInt(lineParts[2]);
					BlockCoord pos = unpackCoord(packedPos).toBlock();
					Block block = Block.BLOCKS[blockId];
					blockChanges.put(pos, block);
				}
			}
		}
		
		this.world = new World(blockChanges);
		
		scanner.close();
	}

	private static int packCoord(Coord3 coord) {
		TxCoord tx = coord.toTx();
		return tx.x << 16 | tx.y << 8 | tx.z;
	}

	private static Coord3 unpackCoord(int pack) {
		int x = pack >> 16 & 0xFF;
		int y = pack >> 8 & 0xFF;
		int z = pack & 0xFF;
		return Coord3.fromTx(x, y, z);
	}

}
