package com.zarkonnen.spacegen;

public class Structure {
	StructureType type;
	Civ builders;
	int buildTime;

	public Structure(StructureType type, Civ builders, int buildTime) {
		this.type = type;
		this.builders = builders;
		this.buildTime = buildTime;
	}

	@Override
	public String toString() {
		return type.name + ", built by the " + builders.name + " in " + buildTime;
	}
}
