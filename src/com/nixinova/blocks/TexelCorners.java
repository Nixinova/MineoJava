package com.nixinova.blocks;

import com.nixinova.Vector3;
import com.nixinova.coords.SubBlockCoord;
import com.nixinova.graphics.Texture;

/*
 * Corners of a block:
 * A--B
 * |  |
 * C--D
 */

public class TexelCorners {

	public SubBlockCoord cornerA, cornerB, cornerC, cornerD;
	public SubBlockCoord.Vector horizVec, vertVec;
	/** SubBlockCoord[cornerID: 0,1,2,3][xPosition][yPosition] */
	public SubBlockCoord[][][] texCornersList;

	private final byte SIZE = Texture.SIZE;

	public TexelCorners(BlockCorners blockCorners) {
		this.cornerA = blockCorners.cornerA;
		this.cornerB = blockCorners.cornerB;
		this.cornerC = blockCorners.cornerC;
		this.cornerD = blockCorners.cornerD;

		var bHorizVec = Vector3.blockDistance(cornerA, cornerB);
		var bVertVec = Vector3.blockDistance(cornerA, cornerC);
		// Calculate texel vectors to be block vector / SIZE
		this.horizVec = new SubBlockCoord.Vector(bHorizVec.x / SIZE, bHorizVec.y / SIZE, bHorizVec.z / SIZE);
		this.vertVec = new SubBlockCoord.Vector(bVertVec.x / SIZE, bVertVec.y / SIZE, bVertVec.z / SIZE);

		this.toTexelCornersArray();
	}

	public SubBlockCoord[] getTexelCorners(int x, int y) {
		return texCornersList[x][y];
	}

	/** Convert these 4 block corners into an array grid of texel corners. */
	private void toTexelCornersArray() {
		texCornersList = new SubBlockCoord[SIZE][SIZE][]; // TEXTURE_SIZE^2 # of instances of 4 corners

		for (byte texX = 0; texX < SIZE; texX++) {
			for (byte texY = 0; texY < SIZE; texY++) {
				// Calculate horiz and vert offset for the start of this texture coordinate
				// Note: 3D world space so 'horiz' and 'vert' are relative & cross the Z axis (i.e., are not just X and Y)
				var dHoriz = new SubBlockCoord.Vector(horizVec.x * texX, horizVec.y * texX, horizVec.z * texX);
				var dVert = new SubBlockCoord.Vector(vertVec.x * texY, vertVec.y * texY, vertVec.z * texY);

				// Current texture coord starting offset vs start position (corner A)
				var dStart = Vector3.add(dHoriz, dVert);

				// Calculate texture coord corners
				// Apply offset to corner A and then apply horiz and vert vectors to B,C,D
				SubBlockCoord texA = cornerA.applyVector(dStart);
				SubBlockCoord texB = texA.applyVector(horizVec);
				SubBlockCoord texC = texA.applyVector(vertVec);
				SubBlockCoord texD = texB.applyVector(vertVec);

				// Save texel corners to array
				var texCorners = BlockCorners.abcdToArray(texA, texB, texC, texD);
				texCornersList[texX][texY] = texCorners;
			}
		}

	}

}
