package com.zarkonnen.spacegen;

public class PlanetSprite extends Sprite {
	final Planet p;

	public PlanetSprite(Planet p) {
		this.p = p;
		img = Imager.get(p);
		x = p.x * 200;
		y = p.y * 200;
	}
}
