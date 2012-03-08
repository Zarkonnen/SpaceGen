package com.zarkonnen.spacegen;

import java.awt.Color;
import java.awt.Graphics2D;

public class GameDisplay {
	final GameWorld w;
	final int width;
	final int height;

	GameDisplay(GameWorld w, int width, int height) {
		this.w = w;
		this.width = width;
		this.height = height;
	}

	void draw(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);

		int x = 0;
		for (Civ c : w.sg.civs) { for (SentientType st : c.fullMembers) {
			g.drawImage(Imager.getImg(st), 100 + (x += 40), 100, null);
		}}
	}
}
