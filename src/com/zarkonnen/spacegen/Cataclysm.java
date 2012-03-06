package com.zarkonnen.spacegen;

public enum Cataclysm {
	NOVA("nova", "The star of $name goes nova, scraping the planet clean of all life!"),
	VOLCANIC_ERUPTIONS("series of volcanic eruptions", "Massive volcanic eruptions on $name eradicate all life on the planet!"),
	AXIAL_SHIFT("shift in the planet's orbital axis", "A shift in the orbital axis of $name spells doom for all life on the planet!"),
	METEORITE_IMPACT("massive asteroid impact", "All life on $name is killed off by a massive asteroid impact!"),
	NANOFUNGAL_BLOOM("nanofungal bloom", "A nanofungal bloom consumes all other life on $name before itself dying from a lack of nutrients!"),
	PSIONIC_SHOCKWAVE("psionic shockwave", "A psionic shockwave of unknown origin passes through $name, instantly stopping all life!");
	
	final String name;
	final String desc;

	private Cataclysm(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}
}
