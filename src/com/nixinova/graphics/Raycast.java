package com.nixinova.graphics;

import com.nixinova.Vector3;
import com.nixinova.blocks.HoveredBlock;
import com.nixinova.coords.BlockCoord;
import com.nixinova.coords.Coord1;
import com.nixinova.coords.Coord3;
import com.nixinova.main.Game;

public class Raycast {

	public static boolean isBlockVisibleToPlayer(Game game, int blockX, int blockY, int blockZ) {
		final double stepSize = Coord1.blockToPx(0.5);

		// Get camera position of player
		var camPos = game.controls.getCameraPosition().toPx();

		// Define the corners of the block
		int[][] cornerOffsets = {
			{ 0, 0, 0 },
			{ 1, 0, 0 },
			{ 0, 1, 0 },
			{ 0, 0, 1 },
			{ 1, 1, 0 },
			{ 1, 0, 1 },
			{ 0, 1, 1 },
			{ 1, 1, 1 },
		};

		// Check visibility for each corner
		for (int[] offsets : cornerOffsets) {
			int blockCornerX = blockX + offsets[0];
			int blockCornerY = blockY + offsets[1];
			int blockCornerZ = blockZ + offsets[2];

			// Calculate direction vector from player to corner
			double distX = Coord1.blockToPx(blockCornerX) - camPos.x;
			double distY = Coord1.blockToPx(blockCornerY) - camPos.y;
			double distZ = Coord1.blockToPx(blockCornerZ) - camPos.z;

			// Normalize the direction vector
			double length = Math.sqrt(distX * distX + distY * distY + distZ * distZ);
			double vecX = distX / length;
			double vecY = distY / length;
			double vecZ = distZ / length;

			// Number of steps to reach the block
			int stepCount = (int) (length / stepSize);

			// Raycasting loop
			double curX = camPos.x;
			double curY = camPos.y;
			double curZ = camPos.z;

			BlockCoord lastCur = new BlockCoord(-1, -1, -1);
			for (int i = 0; i < stepCount; i++) {
				curX += vecX * stepSize;
				curY += vecY * stepSize;
				curZ += vecZ * stepSize;

				BlockCoord curBlock = Coord3.fromPx(curX, curY, curZ).toBlock();

				// Ensure we don't check the same block multiple times in a row
				if (curBlock.x == lastCur.x && curBlock.y == lastCur.y && curBlock.z == lastCur.z) {
					// If we've reached the end of the steps loop, the block must not be blocked by anything, so it is visible
					if (i == stepCount - 1)
						return true;
					// Otherwise continue the ray forward
					else
						continue;
				}

				lastCur = curBlock;

				// Success if the target corner is the current block
				if (curBlock.x == blockCornerX && curBlock.y == blockCornerY && curBlock.z == blockCornerZ)
					return true;

				// Fail raycasting if ray enters a solid block
				if (!game.world.isAir(curBlock.x, curBlock.y, curBlock.z))
					break;
			}
		}

		return false;
	}

	public static HoveredBlock getLookingAt(Game game) {
		final double stepSize = 0.5;

		HoveredBlock result = new HoveredBlock(null, null);

		var camPos = game.controls.getCameraPosition().toSubBlock();
		Vector3<Double> vect = game.controls.getViewDirection();

		// Raycasting loop
		double x = camPos.x;
		double y = camPos.y;
		double z = camPos.z;
		double lastX = 0, lastY = 0, lastZ = 0;
		while (true) {
			// Step forward
			x += vect.x * stepSize;
			y += vect.y * stepSize;
			z += vect.z * stepSize;

			BlockCoord curBlock = applyAsBlock(val -> Math.floor(val), x, y, z);

			// Looking outside the world so no block selected
			if (!game.world.isWithinWorld(curBlock.x, curBlock.y, curBlock.z))
				return result;

			// If block is solid, return the hit block and step backward and return the adjacent block
			if (!game.world.isAir(curBlock.x, curBlock.y, curBlock.z)) {
				BlockCoord lastBlock = applyAsBlock(val -> Math.floor(val), lastX, lastY, lastZ);
				result.hoveredBlock = curBlock;
				result.adjacentBlock = lastBlock;
				return result;
			}

			// Update lasts
			lastX = x;
			lastY = y;
			lastZ = z;
		}
	}

	private interface LambdaDouble {
		double call(double val);
	}

	private static BlockCoord applyAsBlock(LambdaDouble operation, double x, double y, double z) {
		return new BlockCoord((int) operation.call(x), (int) operation.call(y), (int) operation.call(z));
	}

}
