package com.nixinova.mineo.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

import com.nixinova.mineo.main.Game;
import com.nixinova.mineo.main.Mineo;
import com.nixinova.mineo.maths.coords.BlockCoord;
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
	public static final float SAVE_VERSION = 2.2f;

	private static final String saveFilePath = Mineo.rootFolder + "/" + SAVE_FILE;

	public World world;
	public Player player;
	public double lookHoriz, lookVert;

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
			double playerLookHoriz = game.controls.getMouseHorizRads();
			double playerLookVert = game.controls.getMouseVertRads();
			writer.write(String.format("%d,%d,%d,%.3f,%.3f\n", playerPos.x, playerPos.y, playerPos.z, playerLookHoriz, playerLookVert));

			// Write each changed block in world
			// Uses run-length encoding:
			// The method of world saving used starts scanning from 0,0,0 and counts how many of the same block appear starting from that point.
			// Once a new block is found, the start point and count are saved. Then we move to the new block and repeat.
			BlockCoord curStartPoint = new BlockCoord(0, 0, 0);
			int lastBlockId = -1;
			int curBlockCount = 0;
			var blockChanges = game.world.getBlockChanges();
			for (var blockCoord : blockChanges.keySet()) {
				Block block = game.world.getBlockAt(blockCoord);
				int blockId = Arrays.asList(Block.BLOCKS).indexOf(block);
				
				// Special case for first block in world: nothing to check against yet
				if (blockCoord.x == World.minCorner.x && blockCoord.y == World.minCorner.y && blockCoord.z == World.minCorner.z) {
					continue;
				}

				// Increment if same, and continue scan unless this is the final item
				if (blockId == lastBlockId) {
					curBlockCount++;
					continue;
				}

				// If block is different, write the start point, scan length, and block ID
				if (lastBlockId >= 0) {
					writer.write(String.format("%d,%d,%d %d %d\n",
						curStartPoint.x, curStartPoint.y, curStartPoint.z, curBlockCount, lastBlockId));
				}
				curBlockCount = 0;
				lastBlockId = blockId;
				curStartPoint = blockCoord;
			}
			// save the last block
			writer.write(String.format("%d,%d,%d %d %d\n",
				curStartPoint.x, curStartPoint.y, curStartPoint.z, curBlockCount, lastBlockId));

			writer.close();
		} catch (IOException err) {
			System.err.println("Could not create game save file.");
		}
	}
	
	public static float loadFileVersion() throws FileNotFoundException {
		File saveFile = new File(saveFilePath);
		
		Scanner scanner = new Scanner(saveFile);
		
		var versionLine = scanner.nextLine().trim();
		float ver = Float.parseFloat(versionLine.split(" ")[1]);
		
		scanner.close();

		return ver;
	}

	private void loadFromFile() throws FileNotFoundException {
		File saveFile = new File(saveFilePath);

		Scanner scanner = new Scanner(saveFile);
		
		// Load the data from the file
		String versionLine = scanner.nextLine().trim();
		String playerLine = scanner.nextLine().trim();
		List<String> blockLines = new ArrayList<>();
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			blockLines.add(line);
		}
		
		// First line - version information
		float ver = Float.parseFloat(versionLine.split(" ")[1]);
		if (ver != SAVE_VERSION) {
			if (Math.floor(ver) == Math.floor(SAVE_VERSION)) {
				System.err.println("Save file is out of date! Some data may not load correctly.");
			} else {
				System.err.println("Save file is critically out of date and cannot be read.");
				scanner.close();
				// set default
				this.player = new Player();
				this.world = new World();
				return;
			}
		}
		
		// Second line - player position
		String[] playerPosData = playerLine.split(",");
		int playerPosX = Integer.parseInt(playerPosData[0]);
		int playerPosY = Integer.parseInt(playerPosData[1]);
		int playerPosZ = Integer.parseInt(playerPosData[2]);
		this.lookHoriz = Double.parseDouble(playerPosData[3]);
		this.lookVert = Double.parseDouble(playerPosData[4]);
		Coord3 pos = Coord3.fromTx(playerPosX, playerPosY, playerPosZ);
		this.player = new Player(pos);

		// Rest of lines - load world data
		Map<BlockCoord, Block> blockChanges = new HashMap<>();
		for (int i = 0; i < blockLines.size() - 1; i++) {
			// Parse line for scan and block info
			String[] lineData = blockLines.get(i).split(" ");
			String[] posData = lineData[0].split(",");
			int startX = Integer.parseInt(posData[0]);
			int startY = Integer.parseInt(posData[1]);
			int startZ = Integer.parseInt(posData[2]);
			int scanCount = Integer.parseInt(lineData[1]);
			int blockId = Integer.parseInt(lineData[2]);
			Block block = Block.BLOCKS[blockId];
			
			// Scan starting from start pos scanCount times, filling in the block
			int count = 0;
			outer:
			for (int x = startX; x < World.maxCorner.x; x++)
				for (int y = startY; y < World.maxCorner.y; y++)
					for (int z = startZ; z < World.maxCorner.z; z++) {
						if (count++ > scanCount) {
							break outer;
						}
						blockChanges.put(new BlockCoord(x, y, z), block);
					}
		}

		this.world = new World(blockChanges);

		scanner.close();
	}

}
