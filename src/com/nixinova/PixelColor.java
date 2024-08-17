package com.nixinova;

import java.awt.Color;

public class PixelColor {
	public static int SELECTION_OUTLINE = 0xFFFFC8;
	
	public static Color fromPixel(int pixelColor) {
		int r = (pixelColor >> 16) & 0xFF;
		int g = (pixelColor >> 8) & 0xFF;
		int b = (pixelColor) & 0xFF;
		return new Color(r, g, b);
	}
}
