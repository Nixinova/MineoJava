package com.nixinova.mineo.ui.graphics;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Texture {
	public static final int SIZE = 8;
	public static final String TEXTURES_FOLDER = "/textures/";

	public static BufferedImage loadImage(String filename) {
		try {
			return ImageIO.read(Texture.class.getResource(TEXTURES_FOLDER + filename + ".png"));
		} catch (Exception err) {
			throw new RuntimeException("Error: Missing file \"" + filename + ".png" + "\"");
		}
	}

	public static BufferedImage loadScaledImage(String filename, double scale) {
		BufferedImage image = loadImage(filename);
		image = Texture.scaleImage(image, scale);
		return image;
	}

	public static Render loadTexture(String name) {
		BufferedImage image = loadImage(name);
		int width = image.getWidth();
		int height = image.getHeight();
		Render result = new Render(width, height);
		image.getRGB(0, 0, width, height, result.getImage(), 0, width);
		return result;
	}

	private static Render scaleTexture(BufferedImage origImg, double scale) {
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
	}

	public static Render scaleTexture(Render orig, double scale) {
		return scaleTexture(orig.getBufferedImage(), scale);
	}

	public static BufferedImage scaleImage(BufferedImage origImg, double scale) {
		return scaleTexture(origImg, scale).getBufferedImage();
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
}
