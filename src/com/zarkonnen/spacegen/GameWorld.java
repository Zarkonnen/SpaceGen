package com.zarkonnen.spacegen;

public class GameWorld {
	SpaceGen sg;
	Stage stage;
	
	int sx, sy, cooldown;

	public GameWorld() {
		stage = new Stage();
	}
	
	public void tick() {
		if (cooldown > 0) { cooldown--; }
		if (sg == null) {
			sg = new SpaceGen(System.currentTimeMillis());
		} else {
			sg.tick();
		}
	}
	
	public boolean subTick() {
		return stage.tick();
	}
}
