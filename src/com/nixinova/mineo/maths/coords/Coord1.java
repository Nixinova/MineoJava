package com.nixinova.mineo.maths.coords;

import com.nixinova.mineo.ui.graphics.Texture;

public class Coord1 {

	private double px;

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
		return new Coord1((int) blockToPx(val));
	}

	public int toBlock() {
		return (int) pxToBlock(this.px);
	}

	// Sub block

	public static Coord1 fromSubBlock(double val) {
		return new Coord1(blockToPx(val));
	}

	public double toSubBlock() {
		return pxToBlock(this.px);
	}

	// Basic quick conversions

	public static double pxToBlock(double px) {
		return px / Texture.SIZE;
	}

	public static double blockToPx(double block) {
		return block * Texture.SIZE;
	}

}
