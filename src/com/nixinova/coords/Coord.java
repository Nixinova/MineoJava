package com.nixinova.coords;

import com.nixinova.Conversion;

public class Coord {

	private double pxX, pxY, pxZ;

	private static final int per = Conversion.PX_PER_BLOCK;

	public Coord() {
		this.pxX = this.pxY = this.pxZ = 0;
	}

	private Coord(double x, double y, double z) {
		this.pxX = x;
		this.pxY = y;
		this.pxZ = z;
	}
	
	// World pixels

	public static Coord fromPx(double x, double y, double z) {
		return new Coord(x, y, z);
	}

	public static Coord fromPx(double n) {
		return Coord.fromPx(n, n, n);
	}

	public static Coord fromPx(PxCoord coord) {
		return Coord.fromPx(coord.x, coord.y, coord.z);
	}

	public PxCoord toPx() {
		return new PxCoord(this.pxX, this.pxY, this.pxZ);
	}
	
	// Texels

	public static Coord fromTx(int x, int y, int z) {
		return new Coord(x, y, z);
	}

	public static Coord fromTx(int n) {
		return Coord.fromTx(n, n, n);
	}

	public static Coord fromTx(TxCoord coord) {
		return Coord.fromTx(coord.x, coord.y, coord.z);
	}

	public TxCoord toTx() {
		return new TxCoord((int) this.pxX, (int) this.pxY, (int) this.pxZ);
	}
	
	// Blocks

	public static Coord fromBlock(int x, int y, int z) {
		return new Coord(x * per, y * per, z * per);
	}

	public static Coord fromBlock(int n) {
		return Coord.fromBlock(n, n, n);
	}

	public static Coord fromBlock(BlockCoord coord) {
		return Coord.fromBlock(coord.x, coord.y, coord.z);
	}

	public BlockCoord toBlock() {
		return new BlockCoord((int) this.pxX / per, (int) this.pxY / per, (int) this.pxZ / per);
	}
	
	// Sub blocks

	public static Coord fromSubBlock(double x, double y, double z) {
		return new Coord(x * per, y * per, z * per);
	}

	public static Coord fromSubBlock(double n) {
		return Coord.fromSubBlock(n, n, n);
	}

	public SubBlockCoord toSubBlock() {
		return new SubBlockCoord(this.pxX / per, this.pxY / per, this.pxZ / per);
	}

}
