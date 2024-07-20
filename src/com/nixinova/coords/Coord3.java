package com.nixinova.coords;

public class Coord3 {

	private Coord1 x, y, z;

	public Coord3() {
		this.x = new Coord1();
		this.y = new Coord1();
		this.z = new Coord1();
	}

	private Coord3(double x, double y, double z) {
		this.x = Coord1.fromPx(x);
		this.y = Coord1.fromPx(y);
		this.z = Coord1.fromPx(z);
	}

	// World pixel

	public static Coord3 fromPx(double x, double y, double z) {
		return new Coord3(x, y, z);
	}

	public static Coord3 fromPx(PxCoord coord) {
		return fromPx(coord.x, coord.y, coord.z);
	}

	public PxCoord toPx() {
		return new PxCoord(this.x.toPx(), this.y.toPx(), this.z.toPx());
	}

	// Texel

	public static Coord3 fromTx(int x, int y, int z) {
		return generate(val -> val, x, y, z); // tx -> px is only a type conversion
	}

	public static Coord3 fromTx(TxCoord coord) {
		return fromTx(coord.x, coord.y, coord.z);
	}

	public TxCoord toTx() {
		return new TxCoord(this.x.toTx(), this.y.toTx(), this.z.toTx());
	}

	// Block

	public static Coord3 fromBlock(int x, int y, int z) {
		return generate(val -> Coord1.fromBlock(val).toPx(), x, y, z);
	}

	public static Coord3 fromBlock(BlockCoord coord) {
		return fromBlock(coord.x, coord.y, coord.z);
	}

	public BlockCoord toBlock() {
		return new BlockCoord(this.x.toBlock(), this.y.toBlock(), this.z.toBlock());
	}

	// Sub block

	public static Coord3 fromSubBlock(double x, double y, double z) {
		return generate(val -> Coord1.fromSubBlock(val).toPx(), x, y, z);
	}

	public static Coord3 fromSubBlock(SubBlockCoord coord) {
		return fromSubBlock(coord.x, coord.y, coord.z);
	}

	public SubBlockCoord toSubBlock() {
		return new SubBlockCoord(this.x.toSubBlock(), this.y.toSubBlock(), this.z.toSubBlock());
	}

	// Helper

	private interface Lambda<T> {
		double convert(T val);
	}

	private static <T> Coord3 generate(Lambda<T> func, T x, T y, T z) {
		return new Coord3(func.convert(x), func.convert(y), func.convert(z));
	}

}
