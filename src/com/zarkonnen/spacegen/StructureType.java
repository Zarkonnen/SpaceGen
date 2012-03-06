package com.zarkonnen.spacegen;

public enum StructureType {
	MILITARY_BASE("military base"),
	MINING_BASE("mining base"),
	SCIENCE_LAB("science lab"),
	CITY("city of spires"),
	VAULT("vast underground vault"),
	PALACE("grand palace"),
	MUSEUM("vast museum"),
	SPACE_HABITAT("space habitat"),
	ARCOLOGY("complex of arcologies");
	
	final String name;

	private StructureType(String name) {
		this.name = name;
	}
	
	static StructureType[] COLONY_ONLY = {
		CITY, VAULT, PALACE, MUSEUM, SPACE_HABITAT, ARCOLOGY
	};
}
