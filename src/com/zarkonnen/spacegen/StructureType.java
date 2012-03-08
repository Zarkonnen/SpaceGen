package com.zarkonnen.spacegen;

public interface StructureType {
	static enum Standard implements StructureType {
		MILITARY_BASE("military base"),
		MINING_BASE("mining base"),
		SCIENCE_LAB("science lab"),
		CITY("city of spires"),
		VAULT("vast underground vault"),
		PALACE("grand palace"),
		MUSEUM("vast museum"),
		SPACE_HABITAT("space habitat"),
		ARCOLOGY("complex of arcologies"),
		ORBITAL_ELEVATOR("orbital elevator"),
		SKULL_PILE("skull pile");

		final String name;
		
		@Override
		public String getName() { return name; }

		private Standard(String name) {
			this.name = name;
		}

		static StructureType[] COLONY_ONLY = {
			CITY, VAULT, PALACE, MUSEUM, SPACE_HABITAT, ARCOLOGY, ORBITAL_ELEVATOR
		};
	}
	
	public String getName();
}
