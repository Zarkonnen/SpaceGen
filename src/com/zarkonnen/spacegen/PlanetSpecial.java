package com.zarkonnen.spacegen;

public enum PlanetSpecial {
	POISON_WORLD(
			"$name has become a poison world.",
			"Poison World: Mineral deposits produce a steady stream of toxins that makes survival extremely difficult."
	) {
		@Override void apply(Planet p) { p.pollution += 4; }
	},
	GEM_WORLD(
			"Deposits of huge gems form on $name.",
			"Gem World: Huge, beautiful gems many metres across can be found in caves and in veins."
	),
	TITANIC_MOUNTAINS(
			"Titanic mountains form on $name.",
			"Titanic Mountains: Mountain ranges so vast they poke out of the atmosphere into space."),
	VAST_CANYONS(
			"Vast canyons are carved on $name.",
			"Vast Canyons: Gashes in the planet's crust so deep they reach the hot mantle, emitting gases noxious to much life but highly nutritious to some."),
	DEEP_IMPENETRABLE_SEAS(
			"Deep impenetrable seas form on $name.",
			"Deep Impenetrable Seas: Hundreds of kilometres down, water turns into ice from pressure."),
	BEAUTIFUL_AURORAE(
			"Beautiful aurorae play across the skies of $name.",
			"Beaufiful Aurorae: The solar wind makes bright displays of flickering green at the planet's poles."),
	GIGANTIC_CAVE_NETWORK(
			"A gigantic network of caves forms on $name.",
			"Gigantic Cave Network: Thousands of kilometres of partially submerged caves riddle the crust of this planet. Very easy to get lost or hide in."),
	TIDALLY_LOCKED_TO_STAR(
			"$name becomes tidally locked to its star.",
			"Tidally Locked: This planet always presents the same side to its sun. There is perpetual day on one side, perpetual night on the other, and a ring of eternal twilight in between."),
	MUSICAL_CAVES(
			"The wind plays across rock formations on $name, producing an eerie music.",
			"Musical Caves: The wind whistles through massive caves and across rocks, producing an eerie music."),
	ICE_PLANET(
			"$name becomes covered with ice.",
			"Ice Planet: Nearly the entire planet is covered in a thick sheet of ice."),
	PERIODICAL_DARKNESS(
			"Due to an accident of orbital mechanics, every 13 years, $name is plunged into total darkness.",
			"Periodical Darkness: Every thirteen years, the orbit of this planet synchronizes with that of a planet closer to its stars, causing a night that lasts for weeks."),
	HUGE_PLAINS(
			"Vast empty plains form on $name.",
			"Vast Empty Plains: Brown, dusty and featureless, their monotony is broken only by the occasional rivulet around which cluster many flying animals.");
	
	final String announcement;
	final String explanation;
	
	void apply(Planet p) {};

	private PlanetSpecial(String announcement, String explanation) {
		this.announcement = announcement;
		this.explanation = explanation;
	}
}
