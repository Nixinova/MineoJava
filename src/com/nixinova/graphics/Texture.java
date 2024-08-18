package com.nixinova.graphics;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Texture {
	public static final int SIZE = 8;
	public static final String TEXTURES_FOLDER = "/textures/";

	public static Render loadTexture(String name) {
		return loadBitmap(name + ".png", 1);
	}

	public static Render loadTexture(String name, double scale) {
		return loadBitmap(name + ".png", scale);
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

	private static Render loadBitmap(String filename, double scale) {
		try {
			BufferedImage origImg = ImageIO.read(Texture.class.getResource(TEXTURES_FOLDER + filename));
			int width = (int) (origImg.getWidth() * scale);
			int height = (int) (origImg.getHeight() * scale);

			BufferedImage scaledImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			AffineTransform at = new AffineTransform();
			at.scale(scale, scale);
			AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			scaledImg = scaleOp.filter(origImg, scaledImg);

			Render result = new Render(width, height);
			scaledImg.getRGB(0, 0, width, height, result.getImage(), 0, width);
			return result;
		} catch (IOException err) {
			throw new RuntimeException("Error: Missing file \"" + filename + "\"");
		}
	}
}
