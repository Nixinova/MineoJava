package com.nixinova.graphics;

import com.nixinova.readwrite.Options;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Textures {
	public static String texturesPath = Options.texturesFolder;

	public static Render Texture(String name) {
		String result = "";
		switch (name) {
			case "sky":
				result = "env/sky.png";
				break;

			case "grass":
				result = "blocks/grass.png";
				break;

			case "stone":
				result = "blocks/stone.png";
				break;

			default:
				result = "blocks/missing_texture.png";
		}
		return loadBitmap(result);
	}

	public static Render grass = Texture("grass");
	public static Render stone = Texture("stone");
	public static Render sky = Texture("sky");

	public static Render loadBitmap(String filename) {
		try {
			System.out.println("Textures:37: " + texturesPath + filename);
			BufferedImage image = ImageIO.read(Textures.class.getResource(String.valueOf(texturesPath) + filename));
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
