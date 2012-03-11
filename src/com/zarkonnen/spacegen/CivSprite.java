package com.zarkonnen.spacegen;

public class CivSprite extends Sprite {
	Civ c;

	public CivSprite(Civ c) {
		this.c = c;
		x = 128 / 2 - 32 / 2;
		y = -32;
		img = Imager.get(c);
	}
}
