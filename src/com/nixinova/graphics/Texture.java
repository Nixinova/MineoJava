package com.nixinova.graphics;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import com.nixinova.Conversion;

public class Texture {
	public static final String TEXTURES_FOLDER = "/textures/";

	private static final int TEX_SIZE = Conversion.PX_PER_BLOCK;

	public static Render loadTexture(String name) {
		return loadBitmap(name + ".png");
	}

	public static int getTexel(Render texture, int texelX, int texelY) {
		int texPx = (texelX & (TEX_SIZE - 1)) + (texelY & (TEX_SIZE - 1)) * TEX_SIZE;
		return texture.pixels[texPx];
	}

	private static Render loadBitmap(String filename) {
		try {
			BufferedImage image = ImageIO.read(Texture.class.getResource(TEXTURES_FOLDER + filename));
			int width = image.getWidth();
			int height = image.getHeight();
			Render result = new Render(width, height);
			image.getRGB(0, 0, width, height, result.pixels, 0, width);
			return result;
		} catch (Exception err) {
			throw new RuntimeException("Error: Missing file \"" + filename + "\"");
		}
	}
}
