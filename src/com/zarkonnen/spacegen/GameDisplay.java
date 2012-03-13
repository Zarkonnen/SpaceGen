/**
Copyright 2012 David Stark

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.zarkonnen.spacegen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class GameDisplay {
	Point ptr;
	final GameWorld w;
	final int width;
	final int height;
	ArrayList<Star> stars = new ArrayList<Star>();
	
	static final BufferedImage[] STAR_IMGS = new BufferedImage[4];
	static {
		BufferedImage img = MediaProvider.it.getImage("misc/star");
		STAR_IMGS[0] = img;
		STAR_IMGS[1] = Imager.scale(img, 1);
		STAR_IMGS[2] = img;
		STAR_IMGS[3] = Imager.scale(img, 3);
	}
	
	class Star {
		int x;
		int y;
		int scale;
		int size;

		public Star(int x, int y, int scale, int size) {
			this.x = x;
			this.y = y;
			this.scale = scale;
			this.size = size;
		}
	}

	GameDisplay(GameWorld w, int width, int height) {
		this.w = w;
		this.width = width;
		this.height = height;
		Random r = new Random();
		for (int i = 0; i < 1200; i++) {
			stars.add(new Star(r.nextInt(2300) - 300, r.nextInt(2300) - 300, r.nextInt(10) + 1, r.nextInt(2) + r.nextInt(2) + 1));
		}
	}

	void draw(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		for (Star p : stars) {
			g.drawImage(STAR_IMGS[p.size], p.x + (-w.stage.camX + width / 2) / p.scale, p.y + (-w.stage.camY + height / 2) / p.scale, null);
		}
		g.translate(-w.stage.camX + width / 2, -w.stage.camY + height / 2);
		w.stage.draw(g);
		
		if (ptr != null) {
			int viewPX = (w.stage.camX + ptr.x - width / 2);
			int viewPY = (w.stage.camY + ptr.y - height / 2);
			Planet closestP = null;
			int dist = 0;

			for (Planet p : w.sg.planets) {
				int pDist = (p.sprite.x + 120 - viewPX) * (p.sprite.x + 120 - viewPX) + (p.sprite.y + 120 - viewPY) * (p.sprite.y + 120 - viewPY);
				if (closestP == null || pDist < dist) {
					closestP = p;
					dist = pDist;
				}
			}

			if (closestP != null && dist < 240 * 240) {
				Draw.text(g, "[bg=333333cc]" + closestP.fullDesc(w.sg), closestP.sprite.x + 170, closestP.sprite.y, 320, 1000);
			}
		}
		
		g.translate(w.stage.camX - width / 2, w.stage.camY - height / 2);
		StringBuilder info = new StringBuilder();
		/*for (int i = w.sg.turnLog.size() - 1; i >= 0; i--) {
			info.append(w.sg.turnLog.get(i)).append("\n");
		}*/
		for (String s : w.sg.turnLog) {
			info.append(s).append("\n");
		}
		Draw.text(g, "[bg=333333cc]" + info.toString() + "", 10, height - 100, width - 20, 100);
		Draw.text(g, "[bg=333333cc]" + w.sg.year + "\nPress space to advance by one event.\nPress R to toggle auto-advance.\nUse arrow keys to move view.\nPoint at things for info.\nPress S to save galaxy details to text file.", 10, 10);

	}
}
