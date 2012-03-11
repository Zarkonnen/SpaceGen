package com.zarkonnen.spacegen;

import java.util.ArrayList;
import java.util.HashMap;

import static com.zarkonnen.spacegen.Stage.*;
import static com.zarkonnen.spacegen.Main.*;

public class PlanetSprite extends Sprite {
	final Planet p;

	public PlanetSprite(Planet p) {
		this.p = p;
		img = Imager.get(p);
		x = p.x * 200;
		y = p.y * 200;
	}
	
	HashMap<Population, ArrayList<Sprite>> popSprites = new HashMap<Population, ArrayList<Sprite>>();
	CivSprite ownerSprite = null;
	HashMap<SpecialLifeform, Sprite> lifeformSprites = new HashMap<SpecialLifeform, Sprite>();
	
	public int popX(Population pop, int index) {
		int total = p.population();
		int step = Math.min(32, 128 / total);
		int d = 0;
		for (Population pop2 : p.inhabitants) {
			if (pop2 == pop) {
				return d + step * index;
			} else {
				d += step * pop2.getSize();
			}
		}
		
		return 0;
	}
	
	public void rearrangePopulation() {
		for (Population pop : p.inhabitants) {
			ArrayList<Sprite> sprites = popSprites.get(pop);
			for (int i = 0; i < sprites.size(); i++) {
				add(move(sprites.get(i), popX(pop, i), 0));
			}
		}
		animate();
	}
}
