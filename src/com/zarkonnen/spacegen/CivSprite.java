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

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.ArrayList;

import static com.zarkonnen.spacegen.Stage.*;
import static com.zarkonnen.spacegen.Main.*;

public class CivSprite extends Sprite {
	static final Color GOLD = new Color(255, 255, 36);
	static final Color COPPER = new Color(202, 139, 70);
	static final BufferedImage C_COIN = MediaProvider.it.tint(MediaProvider.it.getImage("misc/money"), COPPER);
	static final BufferedImage S_COIN = MediaProvider.it.getImage("misc/money");
	static final BufferedImage G_COIN = MediaProvider.it.tint(MediaProvider.it.getImage("misc/money"), GOLD);
	
	static final BufferedImage TECH = MediaProvider.it.tint(MediaProvider.it.getImage("misc/science"), new Color(100, 120, 255, 127));
	static final BufferedImage MIL_TECH = MediaProvider.it.tint(MediaProvider.it.getImage("misc/science"), new Color(255, 100, 120, 127));
	
	static final Color SCI_1 = Color.GREEN;
	static final Color SCI_10 = new Color(0, 255, 100);
	static final Color SCI_100 = new Color(0, 100, 255);
	static final BufferedImage SCIENCE_100 = MediaProvider.it.tint(MediaProvider.it.getImage("misc/science"), SCI_100);
	static final BufferedImage SCIENCE_10 = MediaProvider.it.tint(MediaProvider.it.getImage("misc/science"), SCI_10);
	static final BufferedImage SCIENCE_1 = MediaProvider.it.tint(MediaProvider.it.getImage("misc/science"), SCI_1);
	
	static final BufferedImage SHIP_100 = Imager.scale(MediaProvider.it.getImage("misc/ship"), 24);
	static final BufferedImage SHIP_10 = Imager.scale(MediaProvider.it.getImage("misc/ship"), 20);
	static final BufferedImage SHIP_1 = MediaProvider.it.getImage("misc/ship");
	
	Civ c;

	public CivSprite(Civ c, boolean forShip) {
		this.c = c;
		if (forShip) {
			x = 0;
			y = -32;
		} else {
			x = 160 / 2 - 32 / 2;
			y = -32;
		}
		img = Imager.get(c);
	}
	
	ArrayList<Sprite> resSprites = new ArrayList<Sprite>();
	ArrayList<Sprite> sciSprites = new ArrayList<Sprite>();
	ArrayList<Sprite> fleetSprites = new ArrayList<Sprite>();
	ArrayList<Sprite> techSprites = new ArrayList<Sprite>();
	ArrayList<Sprite> milTechSprites = new ArrayList<Sprite>();
	
	void init() {
		changeRes(0, c.getResources());
		changeScience(0, c.getScience());
		changeFleet(0, c.getMilitary());
		changeTech(c.getTechLevel());
		changeMilTech(c.getWeapLevel());
	}
	
	public void changeTech(int to) {
		while (techSprites.size() > to) {
			add(remove(techSprites.get(techSprites.size() - 1)));
			techSprites.remove(techSprites.size() - 1);
		}
		while (techSprites.size() < to) {
			Sprite ts = new Sprite(TECH, -techSprites.size() * 4 - 16, 16);
			add(add(ts, this));
			techSprites.add(ts);
		}
		for (int i = 0; i < milTechSprites.size(); i++) {
			add(move(milTechSprites.get(i), -techSprites.size() * 4 - i * 4 - 16, 16));
		}
	}
	
	public void changeMilTech(int to) {
		while (milTechSprites.size() > to) {
			add(remove(milTechSprites.get(milTechSprites.size() - 1)));
			milTechSprites.remove(milTechSprites.size() - 1);
		}
		while (milTechSprites.size() < to) {
			Sprite mts = new Sprite(MIL_TECH, -techSprites.size() * 4 - milTechSprites.size() * 4 - 16, 16);
			add(add(mts, this));
			milTechSprites.add(mts);
		}
	}
	
	public void changeScience(int from, int to) {
		int old100   = from / 100;
		int old10 = (from - old100 * 100) / 10;
		int old1 = from - old10 * 10 - old100 * 100;
		
		int new100   = to / 100;
		int new10 = (to - new100 * 100) / 10;
		int new1 = to - new10 * 10 - old100 * 100;
		int newTotal = new100 + new10 + new1;
		int oldSize = sciSprites.size();
		if (oldSize > newTotal) {
			for (int i = newTotal; i < sciSprites.size(); i++) {
				add(remove(sciSprites.get(i)));
			}
			while (sciSprites.size() > newTotal) {
				sciSprites.remove(sciSprites.size() - 1);
			}
		}
		if (oldSize < newTotal) {
			for (int i = oldSize; i < newTotal; i++) {
				Sprite rs = new Sprite();
				if (i >= new100) {
					if (i >= new10) {
						rs.img = SCIENCE_1;
					} else {
						rs.img = SCIENCE_10;
					}
				} else {
					rs.img = SCIENCE_100;
				}
				rs.x = 32 + i * 4;
				rs.y = 16;
				sciSprites.add(rs);
				add(add(rs, this));
			}
		}
		
		for (int i = 0; i < Math.min(oldSize, newTotal); i++) {
			boolean goldThen = i < old100;
			boolean goldNow = i < old10;
			boolean silverThen = i < old10 && i >= old100;
			boolean silverNow = i < new10 && i >= new100;
			boolean copperThen = !goldThen && !silverThen;
			boolean copperNow = !goldNow && !silverNow;
			if (goldNow && !goldThen) {
				add(change(sciSprites.get(i), SCIENCE_100));
			}
			if (silverNow && !silverThen) {
				add(change(sciSprites.get(i), SCIENCE_10));
			}
			if (copperNow && !copperThen) {
				add(change(sciSprites.get(i), SCIENCE_1));
			}
		}		
	}
	
	public void changeRes(int from, int to) {
		int oldGold   = from / 100;
		int oldSilver = (from - oldGold * 100) / 10;
		int oldCopper = from - oldSilver * 10 - oldGold * 100;
		
		int newGold   = to / 100;
		int newSilver = (to - newGold * 100) / 10;
		int newCopper = to - newSilver * 10 - oldGold * 100;
		int newTotal = newGold + newSilver + newCopper;
		int oldSize = resSprites.size();
		if (oldSize > newTotal) {
			for (int i = newTotal; i < resSprites.size(); i++) {
				add(remove(resSprites.get(i)));
			}
			while (resSprites.size() > newTotal) {
				resSprites.remove(resSprites.size() - 1);
			}
		}
		if (oldSize < newTotal) {
			for (int i = oldSize; i < newTotal; i++) {
				Sprite rs = new Sprite();
				if (i >= newGold) {
					if (i >= newSilver) {
						rs.img = C_COIN;
					} else {
						rs.img = S_COIN;
					}
				} else {
					rs.img = G_COIN;
				}
				rs.x = 32 + i * 4;
				rs.y = 0;
				resSprites.add(rs);
				add(add(rs, this));
			}
		}
		
		for (int i = 0; i < Math.min(oldSize, newTotal); i++) {
			boolean goldThen = i < oldGold;
			boolean goldNow = i < oldSilver;
			boolean silverThen = i < oldSilver && i >= oldGold;
			boolean silverNow = i < newSilver && i >= newGold;
			boolean copperThen = !goldThen && !silverThen;
			boolean copperNow = !goldNow && !silverNow;
			if (goldNow && !goldThen) {
				add(change(resSprites.get(i), G_COIN));
			}
			if (silverNow && !silverThen) {
				add(change(resSprites.get(i), S_COIN));
			}
			if (copperNow && !copperThen) {
				add(change(resSprites.get(i), C_COIN));
			}
		}		
	}
	
	public void changeFleet(int from, int to) {
		int old100   = from / 100;
		int old10 = (from - old100 * 100) / 10;
		int old1 = from - old10 * 10 - old100 * 100;
		
		int new100   = to / 100;
		int new10 = (to - new100 * 100) / 10;
		int new1 = to - new10 * 10 - old100 * 100;
		int newTotal = new100 + new10 + new1;
		int oldSize = fleetSprites.size();
		if (oldSize > newTotal) {
			for (int i = newTotal; i < fleetSprites.size(); i++) {
				add(remove(fleetSprites.get(i)));
			}
			while (fleetSprites.size() > newTotal) {
				fleetSprites.remove(fleetSprites.size() - 1);
			}
		}
		if (oldSize < newTotal) {
			for (int i = oldSize; i < newTotal; i++) {
				Sprite rs = new Sprite();
				if (i >= new100) {
					if (i >= new10) {
						rs.img = SHIP_1;
						rs.y = 0;
						rs.x = -4 - i * 4;
					} else {
						rs.img = SHIP_10;
						rs.y = -2;
						rs.x = -4 - i * 4 - 4;
					}
				} else {
					rs.img = SHIP_100;
					rs.y = -4;
					rs.x = -4 - i * 4 - 8;
				}
				fleetSprites.add(rs);
				add(add(rs, this));
			}
		}
		
		for (int i = 0; i < Math.min(oldSize, newTotal); i++) {
			boolean goldThen = i < old100;
			boolean goldNow = i < old10;
			boolean silverThen = i < old10 && i >= old100;
			boolean silverNow = i < new10 && i >= new100;
			boolean copperThen = !goldThen && !silverThen;
			boolean copperNow = !goldNow && !silverNow;
			if (goldNow && !goldThen) {
				add(change(fleetSprites.get(i), SHIP_100));
				add(move(fleetSprites.get(i), -4 - i * 4 - 8, -4));
			}
			if (silverNow && !silverThen) {
				add(change(fleetSprites.get(i), SHIP_10));
				add(move(fleetSprites.get(i), -4 - i * 4 - 4, -2));
			}
			if (copperNow && !copperThen) {
				add(change(fleetSprites.get(i), SHIP_1));
				add(move(fleetSprites.get(i), -4 - i * 4, 0));
			}
		}		
	}
	
	
	/*public void changeFleet(int from, int to) {
		int oldBig = from / 10;
		int oldSmall = from - oldBig * 10;
		
		int newBig = to / 10;
		int newSmall = to - newBig * 10;
		int newTotal = newBig + newSmall;
		int oldSize = fleetSprites.size();
		if (oldSize > newTotal) {
			for (int i = newTotal; i < fleetSprites.size(); i++) {
				add(remove(fleetSprites.get(i)));
			}
			while (fleetSprites.size() > newTotal) {
				fleetSprites.remove(fleetSprites.size() - 1);
			}
		}
		if (oldSize < newTotal) {
			for (int i = oldSize; i < newTotal; i++) {
				Sprite rs = new Sprite();
				if (i >= newBig) {
					rs.img = SHIP_1;
				} else {
					rs.img = SHIP_10;
				}
				rs.x = -i * 4 - 16;
				rs.y = 0;
				fleetSprites.add(rs);
				add(add(rs, this));
			}
		}
		
		for (int i = 0; i < Math.min(oldSize, newTotal); i++) {
			boolean bigThen = i < oldBig;
			boolean bigNow = i < newBig;
			if (bigThen && !bigNow) {
				add(change(fleetSprites.get(i), SHIP_1));
			}
			if (!bigThen && bigNow) {
				add(change(fleetSprites.get(i), SHIP_10));
			}
		}		
	}*/
}
