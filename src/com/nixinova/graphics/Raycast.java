package com.nixinova.graphics;

import com.nixinova.Vector3;
import com.nixinova.coords.BlockCoord;
import com.nixinova.coords.SubBlockCoord;
import com.nixinova.main.Game;

public class Raycast {

	private static final double STEP_SIZE = 0.2;

	public static boolean isBlockVisibleToPlayer(Game game, int blockX, int blockY, int blockZ) {

		// Get player's position
		SubBlockCoord playerPos = game.controls.getCameraPosition().toSubBlock();
		playerPos.y += 0.5; // upround

		// Define the corners of the block
		double[][] corners = {
			{ blockX, blockY, blockZ },
			{ blockX + 1, blockY, blockZ },
			{ blockX, blockY + 1, blockZ },
			{ blockX, blockY, blockZ + 1 },
			{ blockX + 1, blockY + 1, blockZ },
			{ blockX + 1, blockY, blockZ + 1 },
			{ blockX, blockY + 1, blockZ + 1 },
			{ blockX + 1, blockY + 1, blockZ + 1 },
		};

		// Check visibility for each corner
		for (double[] corner : corners) {
			double cornerX = corner[0];
			double cornerY = corner[1];
			double cornerZ = corner[2];

			// Calculate direction vector from player to corner
			double distX = cornerX - playerPos.x;
			double distY = cornerY - playerPos.y;
			double distZ = cornerZ - playerPos.z;

			// Normalize the direction vector
			double length = Math.sqrt(distX * distX + distY * distY + distZ * distZ);
			double vecX = distX / length;
			double vecY = distY / length;
			double vecZ = distZ / length;

			// Number of steps to reach the block
			int steps = (int) (length / STEP_SIZE);

			// Raycasting loop
			double curX = playerPos.x;
			double curY = playerPos.y;
			double curZ = playerPos.z;

			for (int i = 0; i < steps; i++) {
				curX += vecX * STEP_SIZE;
				curY += vecY * STEP_SIZE;
				curZ += vecZ * STEP_SIZE;

				BlockCoord curBlock = applyAsBlock(val -> Math.round(val), curX, curY, curZ);

				// Return early if ray has gone into the ground
				if (curY < game.world.getGroundY(curBlock.x, curBlock.z))
					return false;

				// Continue raycasting if current block is not solid
				if (game.world.isAir(curBlock.x, curBlock.y, curBlock.z))
					continue;

				// Bisible if current block is the target corner
				if (curBlock.x == cornerX && curBlock.y == cornerY && curBlock.z == cornerZ)
					return true;
			}
		}

		return false;
	}

	public static BlockCoord getLookingAt(Game game) {
		var camPos = game.controls.getCameraPosition().toSubBlock();
		Vector3 vect = game.controls.getViewDirection();

		// Raycasting loop
		double x = camPos.x;
		double y = camPos.y;
		double z = camPos.z;
		while (true) {
			x += vect.x * STEP_SIZE;
			y += vect.y * STEP_SIZE;
			z += vect.z * STEP_SIZE;

			BlockCoord curBlock = applyAsBlock(val -> Math.floor(val), x, y, z);

			// Looking outside the world so no block selected
			if (!game.world.isWithinWorld(curBlock.x, curBlock.y, curBlock.z))
				return null;

			// If block is solid, return it's coordinates
			if (!game.world.isAir(curBlock.x, curBlock.y, curBlock.z)) {
				return curBlock;
			}
		}
	}

	private interface LambdaDouble {
		double call(double val);
	}

	private static BlockCoord applyAsBlock(LambdaDouble operation, double x, double y, double z) {
		return new BlockCoord((int) operation.call(x), (int) operation.call(y), (int) operation.call(z));
	}

}
