package com.zarkonnen.spacegen;

public enum Cataclysm {
	NOVA("The star of $name goes nova, scraping the planet clean of all life!"),
	VOLCANIC_ERUPTIONS("Massive volcanic eruptions on $name eradicate all life on the planet!"),
	AXIAL_SHIFT("A shift in the orbital axis of $name spells doom for all life on the planet!"),
	METEORITE_IMPACT("All life on $name is killed off by a massive asteroid impact!"),
	NANOFUNGAL_BLOOM("A nanofungal bloom consumes all other life on $name before itself dying from a lack of nutrients!"),
	PSIONIC_SHOCKWAVE("A psionic shockwave of unknown origin passes through $name, instantly stopping all life!");
	
	final String desc;

	private Cataclysm(String desc) {
		this.desc = desc;
	}
}
