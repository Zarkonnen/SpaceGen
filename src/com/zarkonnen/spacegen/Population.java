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
import static com.zarkonnen.spacegen.Stage.*;
import static com.zarkonnen.spacegen.Main.*;

public class Population {
	SentientType type;
	private int size;
	Planet p;

	public Population(SentientType type, int size, Planet p) {
		this.type = type;
		this.size = size;
		this.p = p;
		create();
	}
	
	@Override
	public String toString() {
		return getSize() + " billion " + (p.getOwner() != null && !p.getOwner().fullMembers.contains(type) ? "enslaved " : "") + type.getName();
	}
	
	public String toUnenslavedString() {
		return getSize() + " billion " + type.getName();
	}
	
	public ArrayList<Sprite> sprites = new ArrayList<Sprite>();
	
	public void increase(int amt) {
		//animate(tracking(p.sprite, delay()));
		size += amt;
		p.sprite.rearrangePopulation();
		for (int i = 0; i < amt; i++) {
			Sprite s = new Sprite();
			s.img = Imager.get(this);
			s.y = 0;
			s.x = p.sprite.popX(this, size - amt + i);
			sprites.add(s);
			add(add(s, p.sprite));
		}
		animate();
		//animate(delay());
	}
	
	public void decrease(int amt) {
		//animate(tracking(p.sprite, delay()));
		for (int i = 0; i < amt; i++) {
			Sprite s = sprites.get(sprites.size() - 1);
			sprites.remove(sprites.size() - 1);
			add(remove(s));
		}
		animate();
		size -= amt;
		p.sprite.rearrangePopulation();
		//animate(delay());
	}
	
	public void eliminate() {
		animate(tracking(p.sprite, delay()));
		for (Sprite s : sprites) {
			add(remove(s));
		}
		sprites.clear();
		animate();
		p.inhabitants.remove(this);
		p.sprite.popSprites.remove(this);
		p.sprite.rearrangePopulation();
		animate(delay());
	}
	
	public void send(Planet target) {
		animate(tracking(p.sprite, delay()));
		Sprite mover = sprites.get(sprites.size() - 1);
		sprites.remove(mover);		
		Population targetPop = null;
		for (Population pop : target.inhabitants) {
			if (pop.type == type) {
				pop.size++;
				target.sprite.rearrangePopulation();
				targetPop = pop;
				break;
			}
		}
		
		if (targetPop == null) {
			targetPop = new Population(type, 0, target);
			targetPop.size = 1;
			target.sprite.rearrangePopulation();
		}
		animate(emancipate(mover));
		animate(tracking(mover, move(mover, target.sprite.x + target.sprite.popX(targetPop, targetPop.size - 1), target.sprite.y)));
		animate(subordinate(mover, target.sprite));
		target.sprite.popSprites.get(targetPop).add(mover);
		
		if (size == 1) {
			p.inhabitants.remove(this);
			p.sprite.popSprites.remove(this);
		} else {
			size--;
		}
		
		p.sprite.rearrangePopulation();
		addUpdateImgs();
	}
	
	public final void create() {
		if (size > 0) {
			animate(tracking(p.sprite, delay()));
		}
		p.inhabitants.add(this);
		p.sprite.popSprites.put(this, sprites);
		p.sprite.rearrangePopulation();
		for (int i = 0; i < getSize(); i++) {
			Sprite s = new Sprite();
			s.img = Imager.get(this);
			s.y = 0;
			s.x = p.sprite.popX(this, i);
			sprites.add(s);
			add(add(s, p.sprite));
		}
		animate();
		animate(delay());
	}
	
	public void update() {
		animate(tracking(p.sprite, delay()));
		for (Sprite s : sprites) { add(change(s, Imager.get(this))); }
		animate();
	}
	
	public void addUpdateImgs() {
		for (Sprite s : sprites) { add(change(s, Imager.get(this))); }
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		int diff = size - this.size;
		if (diff > 0) {
			increase(diff);
		}
		if (diff < 0) {
			decrease(-diff);
		}
	}
}
