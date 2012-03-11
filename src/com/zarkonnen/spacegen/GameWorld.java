package com.zarkonnen.spacegen;

public class GameWorld {
	SpaceGen sg;
	Stage stage;
	
	int sx, sy, cooldown;
	boolean confirm = false;
	boolean confirmNeeded = false;

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
			if (confirm) {
				confirmNeeded = false;
				sg.turnLog.clear();
			}
			return false;
		}
		return stage.tick();
	}
}
