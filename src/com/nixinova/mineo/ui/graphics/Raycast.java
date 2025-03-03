package com.nixinova.mineo.ui.graphics;

import com.nixinova.mineo.io.Options;
import com.nixinova.mineo.main.Game;
import com.nixinova.mineo.maths.Vector3;
import com.nixinova.mineo.maths.coords.BlockCoord;
import com.nixinova.mineo.maths.coords.Coord1;
import com.nixinova.mineo.maths.coords.Coord3;
import com.nixinova.mineo.world.blocks.HoveredBlock;

public class Raycast {
	private static final double VIEWPORT = 0.75;

	public static boolean isBlockVisibleToPlayer(Game game, int blockX, int blockY, int blockZ) {
		final double stepSize = Coord1.blockToPx(0.5);

		// Get camera position of player
		var camPos = game.controls.getCameraPosition().toPx();
		var camRot = game.controls.getViewDirection();

		// Define the corners of the block
		int[][] cornerOffsets = {
			// X, Y, Z
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

			// Early return if block is directly adjacent to camera
			double oneBlock = Coord1.blockToPx(1);
			if (Math.abs(distX) < oneBlock && Math.abs(distY) < oneBlock && Math.abs(distZ) < oneBlock)
				return true;

			// Normalize the direction vector
			double length = Math.sqrt(distX * distX + distY * distY + distZ * distZ);
			double vecX = distX / length;
			double vecY = distY / length;
			double vecZ = distZ / length;

			// Early return if block is not within the player's view
			double dxRot = Math.abs(vecX - camRot.x);
			double dyRot = Math.abs(vecY - camRot.y);
			double dzRot = Math.abs(vecZ - camRot.z);
			if (dxRot > VIEWPORT || dyRot > VIEWPORT || dzRot > VIEWPORT)
				continue;

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
		final double stepSize = 0.1;

		HoveredBlock result = new HoveredBlock(null, null);

		var camPos = game.controls.getCameraPosition().toSubBlock();
		Vector3<Double> vect = game.controls.getViewDirection();

		// Raycasting loop
		double dist = 0;
		double x = camPos.x;
		double y = camPos.y;
		double z = camPos.z;
		BlockCoord lastBlock = null;
		while (true) {
			// Step forward
			x += vect.x * stepSize;
			y += vect.y * stepSize;
			z += vect.z * stepSize;
			dist += stepSize;

			// Exit early if hovered block is out of reach
			if (dist > Options.reach)
				return result;

			// Get block at ray coord
			BlockCoord curBlock = applyAsBlock(val -> Math.floor(val), x, y, z);

			// Looking outside the world so no block selected
			if (!game.world.isWithinWorldBounds(curBlock))
				return result;

			// If block is solid, return the hit block and step backward and return the adjacent block
			if (!game.world.isAir(curBlock)) {
				result.hoveredBlock = curBlock;
				result.adjacentBlock = lastBlock;
				return result;
			}

			// Update lasts
			lastBlock = curBlock;
		}
	}

	private interface LambdaDouble {
		double call(double val);
	}

	private static BlockCoord applyAsBlock(LambdaDouble operation, double x, double y, double z) {
		return new BlockCoord((int) operation.call(x), (int) operation.call(y), (int) operation.call(z));
	}

}
