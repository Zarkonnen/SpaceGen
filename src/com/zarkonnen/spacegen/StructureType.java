package com.zarkonnen.spacegen;

public enum StructureType {
	MILITARY_BASE("military base"),
	MINING_BASE("mining base"),
	SCIENCE_LAB("science lab");
	
	final String name;

	private StructureType(String name) {
		this.name = name;
	}
}
