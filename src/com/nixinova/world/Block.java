package com.nixinova.world;

import com.nixinova.coords.BlockCoord;
import com.nixinova.graphics.Render;
import com.nixinova.graphics.Texture;
import com.nixinova.options.Options;

public class Block {
	public static Block SKY = new Block("env/sky");

	public static Block BEDROCK = new Block("blocks/bedrock");
	public static Block STONE = new Block("blocks/stone");
	public static Block DIRT = new Block("blocks/dirt");
	public static Block GRASS = new Block("blocks/grass");
	public static Block MISSING = new Block("missing_texture");
	
	private Render texture;
	
	public Block(String texture) {
		this.texture = Texture.loadTexture(texture);
	}
	
	public Render getTexture() {
		return this.texture;
	}
	
	public static boolean isInsideWorld(BlockCoord coords) {
		int x = coords.x;
		int z = coords.z;
		int size = Options.worldSize;
		return x >= -size && x <= size && z >= -size && z <= size;
	}

}
