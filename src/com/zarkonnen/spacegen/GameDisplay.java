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
		g.translate(-w.stage.camX + width / 2, -w.stage.camY + height / 2);
		w.stage.draw(g);
		g.translate(w.stage.camX - width / 2, w.stage.camY - height / 2);
		StringBuilder info = new StringBuilder();
		/*for (int i = w.sg.turnLog.size() - 1; i >= 0; i--) {
			info.append(w.sg.turnLog.get(i)).append("\n");
		}*/
		for (String s : w.sg.turnLog) {
			info.append(s).append("\n");
		}
		Draw.text(g, "[bg=333333cc]" + info.toString() + "", 10, height - 100, width - 20, 100);
		Draw.text(g, "[bg=333333cc]" + w.sg.year, 10, 10);
	}
	
	void draw_old(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);

		/*int x = 0;
		for (Civ c : w.sg.civs) { for (SentientType st : c.fullMembers) {
			g.drawImage(Imager.get(st), 100 + (x += 40), 100, null);
		}}*/
		
		// SHITTY FIRST VERSION
		g.translate(-w.sx, -w.sy);
		for (Planet p : w.sg.planets) {
			g.translate(p.x * 220, p.y * 220);
			g.drawImage(Imager.get(p), 0, 0, null);
			if (p.getOwner() != null) {
				g.drawImage(Imager.get(p.getOwner()), 128 / 2 - 32 / 2, - 32, null);
			}
			int offs = 0;
			if (p.population() > 0) {
				int step = 128 / p.population();
				for (Population pop : p.inhabitants) {
					for (int i = 0; i < pop.getSize(); i++) {
						g.drawImage(Imager.get(pop.type), offs, 128 / 2 - 32 / 2, null);
						offs += step;
					}
				}
			}
			offs = 0;
			for (Agent a : w.sg.agents) {
				if (a.p == p) {
					g.drawImage(Imager.get(a), offs, -64, null);
					offs += 32;
				}
			}
			g.translate(-p.x * 220, -p.y * 220);
		}
		
		int viewPX = (w.sx + width / 2) / 220;
		int viewPY = (w.sy + height / 2) / 220;
		Planet closestP = null;
		int dist = 0;
		
		for (Planet p : w.sg.planets) {
			int pDist = (p.x - viewPX) * (p.x - viewPX) + (p.y - viewPY) * (p.y - viewPY);
			if (closestP == null || pDist < dist) {
				closestP = p;
				dist = pDist;
			}
		}
		
		g.translate(closestP.x * 220, closestP.y * 220);
		Draw.text(g, "[bg=333333cc]" + closestP.fullDesc(w.sg), 138, 0, width / 2 - 140, 1000);
		g.translate(-closestP.x * 220, -closestP.y * 220);
		
		g.translate(w.sx, w.sy);
		Draw.text(g, "[bg=333333cc]" + w.sg.year + "", 10, 10);
		StringBuilder info = new StringBuilder();
		for (String s : w.sg.turnLog) {
			info.append(s).append("\n");
		}
		Draw.text(g, "[bg=333333cc]" + info.toString() + "", 10, height - 100, width - 20, 100);
	}
}
