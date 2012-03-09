package com.zarkonnen.spacegen;

import static java.awt.event.KeyEvent.*;

public class GameControls {
	GameDisplay d;
	GameWorld w;
	Input input;

	public GameControls(GameDisplay d, GameWorld w, Input input) {
		this.d = d;
		this.w = w;
		this.input = input;
	}

	public void processInput() {
		if (input.keyDown(VK_UP)) { w.sy -= 40; }
		if (input.keyDown(VK_DOWN)) { w.sy += 40; }
		if (input.keyDown(VK_LEFT)) { w.sx -= 40; }
		if (input.keyDown(VK_RIGHT)) { w.sx += 40; }
		if (input.keyDown(VK_SPACE) && w.cooldown == 0) {
			w.sg.tickUntilSomethingHappens();
			w.cooldown = 5;
		}
	}
}
