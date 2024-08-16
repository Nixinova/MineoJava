package com.nixinova.graphics;

import com.nixinova.blocks.HoveredBlock;
import com.nixinova.coords.BlockCoord;
import com.nixinova.coords.Coord1;
import com.nixinova.coords.Coord3;
import com.nixinova.main.Game;

public class Raycast {
	private static final double VIEWPORT = 0.75;
	private static final float STEP_SIZE = 0.5f;

	public static boolean isBlockVisibleToPlayer(Game game, short blockX, short blockY, short blockZ) {
		final float stepSize = Coord1.blockToPx(STEP_SIZE);

		// Get camera position of player
		var camPos = game.controls.getCameraPosition().toPx();
		var camRot = game.controls.getViewDirection();

		// Define the corners of the block
		byte[][] cornerOffsets = {
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
		for (byte[] offsets : cornerOffsets) {
			short blockCornerX = (short) (blockX + offsets[0]);
			short blockCornerY = (short) (blockY + offsets[1]);
			short blockCornerZ = (short) (blockZ + offsets[2]);

			// Calculate direction vector from player to corner
			float distX = Coord1.blockToPx(blockCornerX) - camPos.x;
			float distY = Coord1.blockToPx(blockCornerY) - camPos.y;
			float distZ = Coord1.blockToPx(blockCornerZ) - camPos.z;

			// Early return if block is directly adjacent to camera
			float oneBlock = Coord1.blockToPx(1);
			if (Math.abs(distX) < oneBlock && Math.abs(distY) < oneBlock && Math.abs(distZ) < oneBlock)
				return true;

			// Normalize the direction vector
			float length = (float) Math.sqrt(distX * distX + distY * distY + distZ * distZ);
			float vecX = distX / length;
			float vecY = distY / length;
			float vecZ = distZ / length;

			// Early return if block is not within the player's view
			float dxRot = (float) Math.abs(vecX - camRot.x);
			float dyRot = (float) Math.abs(vecY - camRot.y);
			float dzRot = (float) Math.abs(vecZ - camRot.z);
			if (dxRot > VIEWPORT || dyRot > VIEWPORT || dzRot > VIEWPORT)
				continue;

			// Number of steps to reach the block
			short stepCount = (short) (length / stepSize);

			// Raycasting loop
			float curX = camPos.x;
			float curY = camPos.y;
			float curZ = camPos.z;

			BlockCoord lastCur = new BlockCoord(-1, -1, -1);
			for (byte i = 0; i < stepCount; i++) {
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

		HoveredBlock result = new HoveredBlock(null, null);

		var camPos = game.controls.getCameraPosition().toSubBlock();
		var vect = game.controls.getViewDirection();

		// Raycasting loop
		float x = camPos.x;
		float y = camPos.y;
		float z = camPos.z;
		float lastX = 0, lastY = 0, lastZ = 0;
		while (true) {
			// Step forward
			x += vect.x * STEP_SIZE;
			y += vect.y * STEP_SIZE;
			z += vect.z * STEP_SIZE;

			BlockCoord curBlock = applyAsBlock(val -> (float) Math.floor(val), x, y, z);

			// Looking outside the world so no block selected
			if (!game.world.isWithinWorld(curBlock.x, curBlock.y, curBlock.z))
				return result;

			// If block is solid, return the hit block and step backward and return the adjacent block
			if (!game.world.isAir(curBlock.x, curBlock.y, curBlock.z)) {
				BlockCoord lastBlock = applyAsBlock(val -> (float) Math.floor(val), lastX, lastY, lastZ);
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

	private interface LambdaFloat {
		float call(float val);
	}

	private static BlockCoord applyAsBlock(LambdaFloat operation, float x, float y, float z) {
		return Coord3.fromSubBlock(operation.call(x), operation.call(y), operation.call(z)).toBlock();
	}

}
