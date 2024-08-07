package com.nixinova.blocks;

import com.nixinova.Vector3;
import com.nixinova.coords.SubBlockCoord;
import com.nixinova.graphics.Texture;

public class TexelCorners {

	public SubBlockCoord cornerA, cornerB, cornerC, cornerD;
	public Vector3<Double> horizVec, vertVec;
	/** SubBlockCoord[cornerID: 0,1,2,3][xPosition][yPosition] */
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

	public SubBlockCoord[] getTexelCorners(int x, int y) {
		return texCornersList[x][y];
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
				var offset = new Vector3<Double>(dHoriz.x + dVert.x, dHoriz.y + dVert.y, dHoriz.z + dVert.z);

				// Calculate texture coord corners
				var texA = new SubBlockCoord(cornerA.x + offset.x, cornerA.y + offset.y, cornerA.z + offset.z);
				var texB = new SubBlockCoord(cornerB.x + offset.x, cornerB.y + offset.y, cornerB.z + offset.z);
				var texC = new SubBlockCoord(cornerC.x + offset.x, cornerC.y + offset.y, cornerC.z + offset.z);
				var texD = new SubBlockCoord(cornerD.x + offset.x, cornerD.y + offset.y, cornerD.z + offset.z);

				// Save texel corners to array
				var texCorners = BlockCorners.abcdToArray(texA, texB, texC, texD);
				texCornersList[texX][texY] = texCorners;
			}
		}

	}

}
