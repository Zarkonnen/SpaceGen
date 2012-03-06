package com.zarkonnen.spacegen;

public enum CivAction {
	EXPLORE_PLANET() {
		@Override
		public void invoke(Civ actor, SpaceGen sg) {
			
		}
	},
	COLONISE_PLANET,
	BUILD_SCIENCE_OUTPOST,
	BUILD_MILITARY_BASE,
	BUILD_MINING_BASE,
	DO_RESEARCH,
	BUILD_WARSHIPS,
	BUILD_CONSTRUCTION;
	
	public void invoke(Civ actor, SpaceGen sg) {}
}
