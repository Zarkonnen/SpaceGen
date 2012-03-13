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

public class GameWorld {
	SpaceGen sg;
	Stage stage;
	
	int sx, sy, cooldown;
	boolean confirm = false;
	boolean confirmNeeded = false;
	boolean autorun = false;
	int confirmWait = 0;

	public GameWorld() {
		stage = new Stage();
	}
	
	public void tick() {
		if (cooldown > 0) { cooldown--; }
		if (sg == null) {
			sg = new SpaceGen(System.currentTimeMillis());
			sg.init();
		} else {
			sg.tick();
		}
	}
	
	public boolean subTick() {
		if (cooldown > 0) { cooldown--; }
		if (confirmNeeded) {
			if (confirm || (autorun && confirmWait++ > 5)) {
				confirmNeeded = false;
				if (autorun) { sg.clearTurnLogOnNewEntry = true; } else { sg.turnLog.clear(); }
				confirmWait = 0;
			}
			return false;
		}
		return stage.tick();
	}
}
