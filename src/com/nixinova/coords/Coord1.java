package com.nixinova.coords;

import com.nixinova.Conversion;

public class Coord1 {

	private double px;

	private static final int PER = Conversion.PX_PER_BLOCK;

	public Coord1() {
		this.px = 0;
	}

	private Coord1(double val) {
		this.px = val;
	}

	// World pixel

	public static Coord1 fromPx(double val) {
		return new Coord1(val);
	}

	public double toPx() {
		return this.px;
	}

	// Texel

	public static Coord1 fromTx(int val) {
		return new Coord1(val);
	}

	public int toTx() {
		return (int) this.px;
	}

	// Block

	public static Coord1 fromBlock(int val) {
		return new Coord1(val * PER);
	}

	public int toBlock() {
		return (int) Math.floor(this.px) / PER;
	}

	// Sub block

	public static Coord1 fromSubBlock(double val) {
		return new Coord1(val * PER);
	}

	public double toSubBlock() {
		return this.px / PER;
	}

}
