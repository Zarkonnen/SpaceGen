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

import java.util.ArrayList;
import java.util.HashMap;

import static com.zarkonnen.spacegen.Stage.*;
import static com.zarkonnen.spacegen.Main.*;

public class PlanetSprite extends Sprite {
	final Planet p;

	public PlanetSprite(Planet p) {
		this.p = p;
		img = Imager.get(p);
		x = p.x * 240;
		y = p.y * 240;
	}
	
	HashMap<Population, ArrayList<Sprite>> popSprites = new HashMap<Population, ArrayList<Sprite>>();
	CivSprite ownerSprite = null;
	HashMap<SpecialLifeform, Sprite> lifeformSprites = new HashMap<SpecialLifeform, Sprite>();
	HashMap<Structure, Sprite> structureSprites = new HashMap<Structure, Sprite>();
	HashMap<Artefact, Sprite> artefactSprites = new HashMap<Artefact, Sprite>();
	HashMap<Plague, Sprite> plagueSprites = new HashMap<Plague, Sprite>();
	
	public int popX(Population pop, int index) {
		int total = p.population();
		int step = Math.min(36, 160 / total);
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
