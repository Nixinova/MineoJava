package com.nixinova.graphics;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Textures {
	private static final String path = "/textures/";

	public static Render floor = loadBitmap("grass.png");
	public static Render ceiling = loadBitmap("stone.png");

	public static Render loadBitmap(String filename) {
		try {
			BufferedImage image = ImageIO.read(Textures.class.getResource(path + filename));
			System.out.println(image);

			int width = image.getWidth();
			int height = image.getHeight();
			Render result = new Render(width, height);
			image.getRGB(0, 0, width, height, result.pixels, 0, width);

			return result;
		} catch (Exception error) {
			System.out.println("Error: Missing file \"" + filename + "\"");
			throw new RuntimeException(error);
		}
	}
}
