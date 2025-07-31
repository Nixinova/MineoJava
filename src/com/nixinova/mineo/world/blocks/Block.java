package com.nixinova.mineo.world.blocks;

public class Block {
	public static final Block AIR = new Block(null);
	public static final Block BEDROCK = new Block("blocks/bedrock");
	public static final Block STONE = new Block("blocks/stone");
	public static final Block DIRT = new Block("blocks/dirt");
	public static final Block GRASS = new Block("blocks/grass_top", "blocks/grass_side", "blocks/dirt");
	public static final Block LOG = new Block("blocks/log_top", "blocks/log_side", "blocks/log_top");
	public static final Block LEAF = new Block("blocks/leaf");

	public static final Block DEBUG = new Block("debug");
	public static final Block MISSING = new Block("missing_texture");
	
	public static final Block[] BLOCKS = {
		AIR,
		BEDROCK,
		STONE, 
		DIRT, 
		GRASS,
		LOG,
		LEAF,
	};

	public enum Side {
		TOP,
		SIDE,
		BOTTOM,
	}

	private BlockTexture topTexture, sideTexture, bottomTexture;

	public Block(String textureName) {
		var texture = textureName == null ? null : new BlockTexture(textureName);
		this.topTexture = this.sideTexture = this.bottomTexture = texture;
	}
	
	public Block(String topTexture, String sideTexture, String bottomTexture) {
		this.topTexture = new BlockTexture(topTexture);
		this.sideTexture = new BlockTexture(sideTexture);
		this.bottomTexture = new BlockTexture(bottomTexture);
	}
	
	public BlockTexture getTexture() {
		// default to the side texture
		return this.sideTexture;
	}

	public BlockTexture getTexture(Side side) {
		switch (side) {
			case TOP:
				return this.topTexture;
			case SIDE:
				return this.sideTexture;
			case BOTTOM:
				return this.bottomTexture;
			default:
				return null;
		}
	}
	
	public BlockTexture getTexture(BlockFace face) {
		switch (face) {
			case YMAX:
				return this.topTexture;
			case YMIN:
				return this.bottomTexture;
			default:
				return this.sideTexture;
		}
	}
}
