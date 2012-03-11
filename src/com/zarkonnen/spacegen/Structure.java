package com.zarkonnen.spacegen;

public class Structure {
	StructureType type;
	Civ builders;
	int buildTime;
	Sprite sprite;

	public Structure(StructureType type, Civ builders, int buildTime) {
		this.type = type;
		this.builders = builders;
		this.buildTime = buildTime;
		this.sprite = new Sprite(Imager.get(this), 0, 0);
	}

	@Override
	public String toString() {
		return type.getName() + ", built by the " + builders.name + " in " + buildTime;
	}
}
