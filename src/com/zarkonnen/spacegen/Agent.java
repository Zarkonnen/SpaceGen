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

import static com.zarkonnen.spacegen.Stage.*;
import static com.zarkonnen.spacegen.Main.*;

public class Agent {
	private Planet location;
	AgentType type;
	int resources;
	int fleet;
	int birth;
	String name;
	SentientType st;
	Civ originator;
	int timer = 0;
	Planet target;
	String color;
	Sprite sprite;
	SpaceGen sg;
	String mType;
	
	public Agent(AgentType type, int birth, String name, SpaceGen sg) {
		this.type = type;
		this.birth = birth;
		this.name = name;
		this.sg = sg;
	}

	public Planet getLocation() {
		return location;
	}

	public void setLocation(Planet location) {
		if (this.location == location) { return; }
		if (this.location != null) {
			boolean passedMe = false;
			for (Agent ag : sg.agents) {
				if (ag == this) { passedMe = true; }
				if (ag.location == this.location && passedMe) {
					add(move(ag.sprite, ag.sprite.x - 36, ag.sprite.y));
				}
			}
		}
		this.location = location;
		if (location == null) {
			add(tracking(sprite, remove(sprite)));
			animate();
			return;
		}
		int locOffset = 0;
		for (Agent ag : sg.agents) {
			if (ag == this) { break; }
			if (ag.location == this.location) {
				locOffset++;
			}
		}
		if (sprite == null) {
			sprite = new Sprite();
			sprite.img = Imager.get(this);
			sprite.x = location.sprite.x + locOffset * 36;
			sprite.y = location.sprite.y - 64;
			add(tracking(location.sprite, add(sprite)));
		} else {
			add(tracking(sprite, move(sprite, location.sprite.x + locOffset * 36, location.sprite.y - 64)));
		}
		animate();
	}
}
