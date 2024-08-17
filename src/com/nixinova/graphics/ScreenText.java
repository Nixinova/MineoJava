package com.nixinova.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.nixinova.coords.BlockCoord;
import com.nixinova.coords.Coord3;
import com.nixinova.coords.SubBlockCoord;
import com.nixinova.main.Game;
import com.nixinova.main.Mineo;
import com.nixinova.options.Options;

public class ScreenText {

	private static final int INDENT = 5;
	private static final int SEP = 15;

	private Graphics graphics;
	private int curLineIndex = 1;

	public ScreenText(Graphics graphics) {
		this.graphics = graphics;
	}

	public void drawMainInfo(Game game) {
		boolean showFullInfo = game.controls.gameInfoShown;

		Font initialFont = graphics.getFont();

		// Draw heading
		float headingFontSize = 1.25f;
		Font headingFont = initialFont.deriveFont(initialFont.getSize() * headingFontSize);
		graphics.setFont(headingFont);
		graphics.setColor(Color.white);
		drawInfoLine(Mineo.TITLE);

		if (!showFullInfo)
			return;

		// Draw info contents
		graphics.setFont(initialFont);
		// FPS
		drawInfoLine("FPS: %d", game.fps);
		// Player block at foot
		var blockPos = game.player.getPosition().toSubBlock();
		var camPos = game.controls.getCameraPosition().toSubBlock();
		drawInfoLine("Position: %.1f / %.1f..%.1f / %.1f", blockPos.x, blockPos.y, camPos.y, blockPos.z);
		// Mouse look angle
		drawInfoLine("Rotation: %.1f / %.1f", game.controls.getMouseHorizDeg(), game.controls.getMouseVertDeg());
		// Block being looked at
		var lookingAt = game.player.getLookingAt().hoveredBlock;
		if (lookingAt != null)
			drawInfoLine("Selected: %d / %d / %d", lookingAt.x, lookingAt.y, lookingAt.z);
	}

	public void drawOptionsWarning() {
		String msg1 = "", msg2 = "", msg3 = "";
		float fileV = Options.fileVersion;
		float curV = Options.OPTIONS_VERSION;
		boolean diffMajor = (int) fileV != (int) curV;
		boolean diffMinor = (int) (fileV * 10) != (int) (curV * 10);
		if (diffMinor) { // only breaking change if major or minor is different
			if (fileV < curV)
				msg1 = "Outdated options version! Client is on " + curV + " while options.txt is on " + fileV + ".";
			else
				msg1 = "Options file too new! options.txt is on " + fileV + " while client is on " + curV + ".";

			if (diffMajor) // breaking changes to the file format
				msg2 = "Data in options.txt which differs from the current version may break or crash your game!";
			else if (diffMinor) // changes to implementation of values
				msg2 = "Data in options.txt which differs from the current version may not work correctly!";

			msg3 = "Delete options.txt in %appdata%\\.mineo and restart the game to refresh the options file.";
		}

		graphics.setColor(diffMajor ? Color.red : diffMinor ? Color.yellow : Color.white);
		graphics.drawString(msg1, INDENT, Display.HEIGHT - SEP * 8);
		graphics.drawString(msg2, INDENT, Display.HEIGHT - SEP * 7);
		graphics.drawString(msg3, INDENT, Display.HEIGHT - SEP * 6);
	}

	private void drawInfoLine(String fStr, Object... args) {
		String fmtdString = String.format(fStr, args);
		graphics.drawString(fmtdString, INDENT, SEP * curLineIndex++);
	}

	private String formatCoord(Coord3 coord) {
		SubBlockCoord pos = coord.toSubBlock();
		String playerX = String.format("%.1f", pos.x);
		String playerY = String.format("%.1f", pos.y);
		String playerZ = String.format("%.1f", pos.z);
		return playerX + " / " + playerY + " / " + playerZ;
	}

}
