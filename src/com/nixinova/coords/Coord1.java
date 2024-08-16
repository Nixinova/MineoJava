package com.nixinova.coords;

import com.nixinova.graphics.Texture;

public class Coord1 {

	private float px;

	public Coord1() {
		this.px = 0;
	}

	private Coord1(double val) {
		this.px = (float) val;
	}

	// World pixel

	public static Coord1 fromPx(double val) {
		return new Coord1(val);
	}

	public float toPx() {
		return this.px;
	}

	// Texel

	public static Coord1 fromTx(int val) {
		return new Coord1(val);
	}

	public short toTx() {
		return (short) Math.round(this.px);
	}

	// Block

	public static Coord1 fromBlock(int val) {
		return new Coord1((int) blockToPx(val));
	}

	public short toBlock() {
		return (short) pxToBlock(this.px);
	}

	// Sub block

	public static Coord1 fromSubBlock(double val) {
		return new Coord1(blockToPx(val));
	}

	public float toSubBlock() {
		return pxToBlock(this.px);
	}

	// Basic quick conversions

	public static float pxToBlock(double px) {
		return (float) px / Texture.SIZE;
	}

	public static float blockToPx(double block) {
		return (float) block * Texture.SIZE;
	}

}
