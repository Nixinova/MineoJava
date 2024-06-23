package com.nixinova.graphics;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import com.nixinova.readwrite.Options;

public class Texture {
	public static Render loadTexture(String name) {
		return loadBitmap(name + ".png");
	}

	private static Render loadBitmap(String filename) {
		try {
			BufferedImage image = ImageIO.read(Texture.class.getResource(Options.texturesFolder + filename));
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
