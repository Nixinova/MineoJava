package com.nixinova.graphics;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Texture {
	public static final int SIZE = 8;
	public static final String TEXTURES_FOLDER = "/textures/";

	public static Render loadTexture(String name) {
		return loadBitmap(name + ".png");
	}

	public static int getTexel(Render texture, int texelX, int texelY, boolean flipX, boolean flipY) {
		int textureX = texelX % SIZE;
		int textureY = texelY % SIZE;
		if (flipX)
			textureX = (SIZE - 1) - textureX;
		if (flipY)
			textureY = (SIZE - 1) - textureY;

		int texPx = textureX + (textureY * SIZE);
		return texture.getPixel(texPx);
	}

	public static int getTexel(Render texture, int texelX, int texelY) {
		return getTexel(texture, texelX, texelY, false, false);
	}

	private static Render loadBitmap(String filename) {
		try {
			BufferedImage image = ImageIO.read(Texture.class.getResource(TEXTURES_FOLDER + filename));
			int width = image.getWidth();
			int height = image.getHeight();
			Render result = new Render(width, height);
			image.getRGB(0, 0, width, height, result.getImage(), 0, width);
			return result;
		} catch (Exception err) {
			throw new RuntimeException("Error: Missing file \"" + filename + "\"");
		}
	}
}
