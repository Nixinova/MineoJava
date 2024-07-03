package com.nixinova.graphics;

import java.awt.Color;
import java.awt.Graphics;

import com.nixinova.coords.BlockCoord;
import com.nixinova.main.Game;
import com.nixinova.main.Mineo;
import com.nixinova.options.Options;

public class ScreenText {

	private static final int INDENT = 5;
	private static final int SEP = 15;

	private Graphics graphics;

	public ScreenText(Graphics graphics) {
		this.graphics = graphics;
	}

	public void drawMainInfo(Game game) {
		BlockCoord playerBlockPos = game.player.position.toBlock();
		String playerX = String.format("%d", playerBlockPos.x);
		String playerY = String.format("%d", playerBlockPos.y);
		String playerZ = String.format("%d", playerBlockPos.z);

		graphics.setColor(Color.white);
		graphics.drawString(Mineo.TITLE, 5, SEP);
		graphics.drawString("FPS: " + String.valueOf(game.fps), 5, SEP * 2);
		graphics.drawString("Block: " + playerX + " / " + playerY + " / " + playerZ, 5, SEP * 3);
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

}
