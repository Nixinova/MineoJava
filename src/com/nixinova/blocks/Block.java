package com.nixinova.blocks;

import com.nixinova.graphics.Render;
import com.nixinova.graphics.Texture;

public class Block {
	public static final Block AIR = new Block(null);
	public static final Block BEDROCK = new Block("blocks/bedrock");
	public static final Block STONE = new Block("blocks/stone");
	public static final Block DIRT = new Block("blocks/dirt");
	public static final Block GRASS = new Block("blocks/grass");

	public static final Block DEBUG = new Block("debug");
	public static final Block MISSING = new Block("missing_texture");
	
	public static final Block[] BLOCKS = {
		AIR,
		BEDROCK,
		STONE, 
		DIRT, 
		GRASS,
	};

	private Render texture;
	private String name;

	public Block(String texture) {
		this.texture = texture == null ? null : Texture.loadTexture(texture);
		this.name = texture;
	}

	public Render getTexture() {
		return this.texture;
	}

	public String getTextureName() {
		return this.name;
	}

}
