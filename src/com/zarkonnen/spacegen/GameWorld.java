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
