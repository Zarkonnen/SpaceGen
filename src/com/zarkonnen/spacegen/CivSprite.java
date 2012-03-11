package com.zarkonnen.spacegen;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.ArrayList;

import static com.zarkonnen.spacegen.Stage.*;
import static com.zarkonnen.spacegen.Main.*;

public class CivSprite extends Sprite {
	static final Color COPPER = new Color(202, 139, 70);
	static final BufferedImage C_COIN = MediaProvider.it.tint(MediaProvider.it.getImage("misc/money"), COPPER);
	static final BufferedImage S_COIN = MediaProvider.it.getImage("misc/money");
	
	static final BufferedImage TECH = MediaProvider.it.tint(MediaProvider.it.getImage("misc/science"), new Color(100, 120, 255, 127));
	static final BufferedImage SCIENCE = MediaProvider.it.tint(MediaProvider.it.getImage("misc/science"), Color.GREEN);
	
	static final Color BIG = new Color(70, 139, 202);
	static final BufferedImage B_SHIP = MediaProvider.it.tint(MediaProvider.it.getImage("misc/ship"), BIG);
	static final BufferedImage S_SHIP = MediaProvider.it.getImage("misc/ship");
	
	Civ c;

	public CivSprite(Civ c) {
		this.c = c;
		x = 160 / 2 - 32 / 2;
		y = -32;
		img = Imager.get(c);
	}
	
	ArrayList<Sprite> resSprites = new ArrayList<Sprite>();
	ArrayList<Sprite> sciSprites = new ArrayList<Sprite>();
	ArrayList<Sprite> fleetSprites = new ArrayList<Sprite>();
	ArrayList<Sprite> techSprites = new ArrayList<Sprite>();
	
	void init() {
		changeRes(0, c.getResources());
		changeScience(c.getScience());
		changeFleet(0, c.getMilitary());
		changeTech(c.getTechLevel());
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
	}
	
	public void changeScience(int to) {
		while (sciSprites.size() > to) {
			add(remove(sciSprites.get(sciSprites.size() - 1)));
			sciSprites.remove(sciSprites.size() - 1);
		}
		while (sciSprites.size() < to) {
			Sprite ss = new Sprite(SCIENCE, 32 + sciSprites.size() * 4, 16);
			add(add(ss, this));
			sciSprites.add(ss);
		}
	}
	
	public void changeRes(int from, int to) {
		int oldSilver = from / 10;
		int oldCopper = from - oldSilver * 10;
		
		int newSilver = to / 10;
		int newCopper = to - newSilver * 10;
		int newTotal = newSilver + newCopper;
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
				if (i >= newSilver) {
					rs.img = C_COIN;
				} else {
					rs.img = S_COIN;
				}
				rs.x = 32 + i * 4;
				rs.y = 0;
				resSprites.add(rs);
				add(add(rs, this));
			}
		}
		
		for (int i = 0; i < Math.min(oldSize, newTotal); i++) {
			boolean silverThen = i < oldSilver;
			boolean silverNow = i < newSilver;
			if (silverThen && !silverNow) {
				add(change(resSprites.get(i), C_COIN));
			}
			if (!silverThen && silverNow) {
				add(change(resSprites.get(i), S_COIN));
			}
		}		
	}
	
	public void changeFleet(int from, int to) {
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
					rs.img = S_SHIP;
				} else {
					rs.img = B_SHIP;
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
				add(change(fleetSprites.get(i), S_SHIP));
			}
			if (!bigThen && bigNow) {
				add(change(fleetSprites.get(i), B_SHIP));
			}
		}		
	}
}
