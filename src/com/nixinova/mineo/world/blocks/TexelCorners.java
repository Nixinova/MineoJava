package com.nixinova.mineo.world.blocks;

import com.nixinova.mineo.maths.Vector3;
import com.nixinova.mineo.maths.coords.SubBlockCoord;
import com.nixinova.mineo.ui.graphics.Texture;

/*
 * Corners of a block:
 * 
 * A--B
 * |  |
 * C--D
 */

public class TexelCorners {

	public SubBlockCoord cornerA, cornerB, cornerC, cornerD;
	public Vector3<Double> horizVec, vertVec;
	/** SubBlockCoord[xPosition][yPosition][cornerID: 0,1,2,3] */
	public SubBlockCoord[][][] texCornersList;

	private final int SIZE = Texture.SIZE;

	public TexelCorners(BlockCorners blockCorners) {
		this.cornerA = blockCorners.cornerA;
		this.cornerB = blockCorners.cornerB;
		this.cornerC = blockCorners.cornerC;
		this.cornerD = blockCorners.cornerD;

		Vector3<Double> blockHorizVec = Vector3.blockDistance(cornerA, cornerB);
		Vector3<Double> blockVertVec = Vector3.blockDistance(cornerA, cornerC);
		// Calculate texel vectors to be block vector / SIZE
		this.horizVec = new Vector3<Double>(blockHorizVec.x / SIZE, blockHorizVec.y / SIZE, blockHorizVec.z / SIZE);
		this.vertVec = new Vector3<Double>(blockVertVec.x / SIZE, blockVertVec.y / SIZE, blockVertVec.z / SIZE);

		this.toTexelCornersArray();
	}

	public SubBlockCoord[] getTexelCorners(int x, int y, int scale) {
	    // prevent out-of-bounds
	    int x2 = Math.min(x + scale - 1, texCornersList.length - 1);
	    int y2 = Math.min(y + scale - 1, texCornersList[0].length - 1);

	    // Get four corners derived from multiple texels
	    SubBlockCoord texA = texCornersList[x][y][0];
	    SubBlockCoord texB = texCornersList[x2][y][1];
	    SubBlockCoord texC = texCornersList[x][y2][3];
	    SubBlockCoord texD = texCornersList[x2][y2][2];

	    return BlockCorners.abcdToArray(texA, texB, texC, texD);
	}

	/** Convert these 4 block corners into an array grid of texel corners. */
	private void toTexelCornersArray() {
		texCornersList = new SubBlockCoord[SIZE][SIZE][]; // TEXTURE_SIZE^2 # of instances of 4 corners

		for (int texX = 0; texX < SIZE; texX++) {
			for (int texY = 0; texY < SIZE; texY++) {
				// Calculate horiz and vert offset for the start of this texture coordinate
				// Note: 3D world space so 'horiz' and 'vert' are relative & cross the Z axis (i.e., are not just X and Y)
				var dHoriz = new Vector3<Double>(horizVec.x * texX, horizVec.y * texX, horizVec.z * texX);
				var dVert = new Vector3<Double>(vertVec.x * texY, vertVec.y * texY, vertVec.z * texY);

				// Current texture coord starting offset vs start position (corner A)
				Vector3<Double> dStart = Vector3.add(dHoriz, dVert);

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
