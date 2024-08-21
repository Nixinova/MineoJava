package com.nixinova.graphics;

import java.awt.Color;

public class TextColorScheme {
	public Color text;
	public Color fill;
	public Color border;

	public TextColorScheme(Color text, Color fill, Color border) {
		this.text = text;
		this.fill = fill;
		this.border = border;
	}

	public TextColorScheme(Color text) {
		this(text, Color.white, Color.black);
	}

	public TextColorScheme(Color text, Color border) {
		this(text, text, border);
	}
}
