package com.zarkonnen.spacegen;

public class GameWorld {
	SpaceGen sg;
	
	int sx, sy, cooldown;

	public GameWorld() {
		sg = new SpaceGen(System.currentTimeMillis());
		/*int bound = 650;
		while (!sg.interesting(bound)) {
			sg.tick();
		}
		sg.l("");
		sg.l("");
		sg.describe();
		
		for (String le : sg.log) {
			System.out.println(le);
		}*/
	}
	
	public void tick() {
		if (cooldown > 0) { cooldown--; }
	}
}
