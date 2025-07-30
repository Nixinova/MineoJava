package com.nixinova.mineo.world.blocks;

import com.nixinova.mineo.ui.graphics.Render;
import com.nixinova.mineo.ui.graphics.Texture;

public class BlockTexture {
	public Render texture;
	public String name;
	
	public BlockTexture(String texture) {
		this.texture = Texture.loadTexture(texture);
		this.name = texture;
	}
}
