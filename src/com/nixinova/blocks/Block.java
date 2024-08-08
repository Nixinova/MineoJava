package com.nixinova.blocks;

import com.nixinova.graphics.Render;
import com.nixinova.graphics.Texture;

public class Block {
	public static Block SKY = new Block("env/sky");

	public static Block AIR = new Block(null);
	public static Block BEDROCK = new Block("blocks/bedrock");
	public static Block STONE = new Block("blocks/stone");
	public static Block DIRT = new Block("blocks/dirt");
	public static Block GRASS = new Block("blocks/grass");
	public static Block DEBUG = new Block("debug");
	public static Block MISSING = new Block("missing_texture");

	private Render texture;

	public Block(String texture) {
		this.texture = texture == null ? null : Texture.loadTexture(texture);
	}

	public Render getTexture() {
		return this.texture;
	}

}
