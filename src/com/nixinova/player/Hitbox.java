package com.nixinova.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nixinova.Vector3;
import com.nixinova.coords.Coord1;
import com.nixinova.coords.Coord3;
import com.nixinova.coords.SubBlockCoord;
import com.nixinova.world.World;

public class Hitbox {

	public static enum Corner {
		// capitalisation denotes min/max
		FOOT_xz, FOOT_Xz, FOOT_xZ, FOOT_XZ,
		HEAD_xz, HEAD_Xz, HEAD_xZ, HEAD_XZ,
	}

	public static class CornersList {
		public List<Corner> list = new ArrayList<>();

		public boolean contains(Corner corner) {
			return list.contains(corner);
		}

		public boolean containsAll(Corner... providedCorners) {
			for (Corner corner : providedCorners) {
				if (!list.contains(corner))
					return false;
			}
			return true;
		}
	}

	private static Map<Corner, Vector3<Integer>> playerCornerOffsets = new HashMap<>() {
		private static final long serialVersionUID = 1L;
		{
			put(Corner.FOOT_xz, new Vector3<>(-1, 0, -1));
			put(Corner.FOOT_Xz, new Vector3<>(+1, 0, -1));
			put(Corner.FOOT_xZ, new Vector3<>(-1, 0, +1));
			put(Corner.FOOT_XZ, new Vector3<>(+1, 0, +1));

			put(Corner.HEAD_xz, new Vector3<>(-1, 1, -1));
			put(Corner.HEAD_Xz, new Vector3<>(+1, 1, -1));
			put(Corner.HEAD_xZ, new Vector3<>(-1, 1, +1));
			put(Corner.HEAD_XZ, new Vector3<>(+1, 1, +1));
		}
	};

	public static CornersList getCollisionPoints(World world, Coord3 position) {
		CornersList collisionCorners = new CornersList();

		SubBlockCoord blockPos = position.toSubBlock();

		// check each corner of the player's hitbox for being inside a block at the new position
		for (var hitboxEntry : playerCornerOffsets.entrySet()) {
			Corner collisionPoint = hitboxEntry.getKey();
			Vector3<Integer> corner = hitboxEntry.getValue();
			
			// get block at each corner of the player's hitbox
			int posX = Coord1.fromSubBlock(blockPos.x + Player.PLAYER_WIDTH / 2 * corner.x).toBlock();
			int posY = Coord1.fromSubBlock(blockPos.y + Player.PLAYER_HEIGHT * corner.y).toBlock();
			int posZ = Coord1.fromSubBlock(blockPos.z + Player.PLAYER_WIDTH / 2 * corner.z).toBlock();

			// if player ends up inside a block then add its corner to list
			if (!world.isAir(posX, posY, posZ))
				collisionCorners.list.add(collisionPoint);
		}

		return collisionCorners;
	}

}
