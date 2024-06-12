package com.nixinova.graphics;

import com.nixinova.readwrite.Options;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Textures {
	public static String texturesPath = Options.texturesFolder;

	public static Render Texture(String name) {
		String file = name != null ? name : "blocks/missing_texture";
		return loadBitmap(file + ".png");
	}

	public static Render stone = Texture("blocks/stone");
	public static Render bedrock = Texture("blocks/bedrock");
	public static Render grass = Texture("blocks/grass");
	public static Render dirt = Texture("blocks/dirt");
	public static Render sky = Texture("env/sky");
	public static Render none = Texture(null); // missing texture/other

	public static Render loadBitmap(String filename) {
		try {
			BufferedImage image = ImageIO.read(Textures.class.getResource(String.valueOf(texturesPath) + filename));
			int width = image.getWidth();
			int height = image.getHeight();
			Render result = new Render(width, height);
			image.getRGB(0, 0, width, height, result.pixels, 0, width);
			return result;
		} catch (Exception error) {
			System.out.println("(Textures:44) Error: Missing file \"" + filename + "\"");
			throw new RuntimeException(error);
		}
	}
}
