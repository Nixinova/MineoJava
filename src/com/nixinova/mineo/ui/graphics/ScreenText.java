package com.nixinova.mineo.ui.graphics;

import java.awt.Color;
import java.awt.Graphics;

import com.nixinova.mineo.main.Game;
import com.nixinova.mineo.main.Mineo;
import com.nixinova.mineo.ui.display.GameDisplay;

public class ScreenText {

	private static final int LEFT_MARGIN = 5;
	private static final int TOP_MARGIN = 5;

	private Graphics graphics;
	private int curLineIndex = 0;

	public ScreenText(Graphics graphics) {
		this.graphics = graphics;
	}

	public void drawMainInfo(Game game) {
		var headerFont = FontGraphics.FONT_150;
		var infoFont = FontGraphics.FONT_100;

		boolean showFullInfo = game.controls.gameInfoShown;

		// Draw heading
		drawInfoLine(headerFont, Mineo.TITLE);

		if (!showFullInfo)
			return;

		// Draw info contents
		// FPS
		drawInfoLine(infoFont, "FPS: %d", game.fps);
		// Player block at foot
		var blockPos = game.player.getPosition().toSubBlock();
		var camPos = game.controls.getCameraPosition().toSubBlock();
		drawInfoLine(infoFont, "Position: %.1f / %.1f..%.1f / %.1f", blockPos.x, blockPos.y, camPos.y, blockPos.z);
		// Mouse look angle
		drawInfoLine(infoFont, "Rotation: %.1f / %.1f",
			game.controls.getMouseHorizDeg(), game.controls.getMouseVertDeg());
		// Block being looked at
		var lookingAt = game.player.getLookingAt().hoveredBlock;
		if (lookingAt != null)
			drawInfoLine(infoFont, "Selected: %d / %d / %d", lookingAt.x, lookingAt.y, lookingAt.z);
	}
	
	public void checkThenDrawSavedDataWarning(String type, float fileV, float curV) {
		var warningFont = FontGraphics.FONT_200;

		String msg1 = "", msg2 = "", msg3 = "";
		boolean diffMajor = (int) fileV != (int) curV;
		boolean diffMinor = (int) (fileV * 10) != (int) (curV * 10);
		if (diffMinor) { // only breaking change if major or minor is different
			if (fileV < curV)
				msg1 = String.format("Outdated %1$s version! Client is on %2$.2f while %1$s is on %3$.2f.", type, curV, fileV);
			else
				msg1 = String.format("%1$s file is too new! %2$.2f is newer than expected version %3$.2f.", type, curV, fileV);

			if (diffMajor) // breaking changes to the file format
				msg2 = String.format("Data in %s which differs from the current version may break or crash your game!", type);
			else if (diffMinor) // changes to implementation of values
				msg2 = String.format("Data in %s which differs from the current version may not work correctly!", type);

			msg3 = String.format("Update %s in %%appdata%%\\.mineo and restart the game to refresh the file.", type);
		}

		Color col = diffMajor ? Color.red : diffMinor ? Color.yellow : Color.white;
		var textScheme = new TextColorScheme(col, Color.black);
		int startX = GameDisplay.WIDTH / 5;
		curLineIndex++;
		warningFont.drawStringOutlined(graphics, msg1, startX, 20 * curLineIndex++, textScheme);
		warningFont.drawStringOutlined(graphics, "  " + msg2, startX, 20 * curLineIndex++, textScheme);
		warningFont.drawStringOutlined(graphics, "  " + msg3, startX, 20 * curLineIndex++, textScheme);
	}

	private void drawInfoLine(FontGraphics fg, String fStr, Object... args) {
		String fmtdString = String.format(fStr, args);
		fg.drawString(graphics, fmtdString, Color.white, LEFT_MARGIN, TOP_MARGIN + 15 * curLineIndex++);
	}

}
