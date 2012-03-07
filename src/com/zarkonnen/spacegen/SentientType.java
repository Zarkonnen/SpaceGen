package com.zarkonnen.spacegen;

import static com.zarkonnen.spacegen.CivAction.*;

public enum SentientType {
	ANTOIDS(
		"Antoids",
		"Small, industrious and well-organised, these creatures' greatest weakness is their ever-ballooning population.",
		"cluster of breeding pits",
		" Antenna",
		new String[] { "Kak", "Krk'", "Tk", "Tch'", "Tk'k" },
		new String[] { "erlak", "kra", "hkt", "ukk", "kraa" },
		EXPLORE_PLANET, COLONISE_PLANET, COLONISE_PLANET, COLONISE_PLANET, BUILD_MINING_BASE, BUILD_MINING_BASE, BUILD_CONSTRUCTION),
	URSOIDS(
		"Ursoids",
		"Ursoids are always at war with someone, or everyone, or one another.",
		"barracks",
		"muzzle",
		new String[] { "Ur", "Ber'", "Gro", "Brm'", "Or" },
		new String[] { "sus", "mog", "rr", "orr", "serk" },
		EXPLORE_PLANET, EXPLORE_PLANET, BUILD_MILITARY_BASE, BUILD_WARSHIPS, BUILD_WARSHIPS),
	DWARFOIDS(
		"Dwarfoids",
		"Dwarfoids are fond of drink and industry - especially building strange contraptions.",
		"great hall",
		"beard",
		new String[] { "Urist", "Grolin", "Gnolin", "Minin", "Balin" },
		new String[] { " McUrist", " Thundersson", " Longbeard", " Bronzepick", " Carpwrestler" },
		EXPLORE_PLANET, COLONISE_PLANET, BUILD_MINING_BASE, BUILD_MINING_BASE, BUILD_MINING_BASE, DO_RESEARCH, BUILD_CONSTRUCTION),
	CATOIDS(
		"Catoids",
		"Catoids are always curious and often cruel.",
		"torture chamber",
		"paw",
		new String[] { "Mrr", "Mrw", "Mmbrr", "Rrre", "Mee" },
		new String[] { "oaw", "ow", "orr", "reww", "ar" },
		EXPLORE_PLANET, COLONISE_PLANET, EXPLORE_PLANET, EXPLORE_PLANET, BUILD_WARSHIPS, BUILD_WARSHIPS),
	TROLLOIDS(
		"Trolloids",
		"Trolloids have lost the ability to reproduce naturally, and compensate for this with a focus on science and paranoia.",
		"gene library",
		"rock",
		new String[] { "Unkut", "Rufkut", "Finkut"  },
		new String[] { " Sedimin", " Aa", " Igneous", " Geode" },
		EXPLORE_PLANET, BUILD_MILITARY_BASE, BUILD_MILITARY_BASE, BUILD_SCIENCE_OUTPOST, BUILD_SCIENCE_OUTPOST, DO_RESEARCH),
	DEEP_DWELLERS(
		"Deep Dwellers",
		"Deep Dwellers live in the deep oceans or in deep caves, hiding. Their presence can go unnoticed for a long time.",
		"deep dome",
		"crest",
		new String[] { "Zursu", "Uln", "Wi", "Paraa", "Nio" },
		new String[] { "pram", "ivex", "lon", "ix", "it" },
		BUILD_SCIENCE_OUTPOST, BUILD_MILITARY_BASE, BUILD_MILITARY_BASE, BUILD_MINING_BASE, BUILD_MINING_BASE, COLONISE_PLANET),
	PARASITES(
		"Parasites",
		"Parasites operate by infesting the brains of other sentients and using their bodies as vehicles.",
		"biolab",
		" Tentacle",
		new String[] { "Dark ", "Shining ", "Slithering ", "Grasping ", "Insidious " },
		new String[] { "Tentacle", "Tentril", "Beak", "Ovipositor", "Needle" },
		EXPLORE_PLANET, EXPLORE_PLANET, EXPLORE_PLANET, EXPLORE_PLANET, COLONISE_PLANET, DO_RESEARCH),
	TERRANS(
		"Terrans",
		"Terrans are pretty adventurous but otherwise fairly average.",
		"space academy",
		"beard",
		new String[] { "James T. ", "Jason ", "Annette ", "Asimov " },
		new String[] { "Asimov", "Cairn", "Runge-Kutta", "Johnson" },
		EXPLORE_PLANET, EXPLORE_PLANET, COLONISE_PLANET, BUILD_SCIENCE_OUTPOST, BUILD_MILITARY_BASE, BUILD_MINING_BASE, BUILD_WARSHIPS)
		;
	
	final String name;
	final String desc;
	final String pSuffix;
	final String[] nameStarts;
	final String[] nameEnds;
	final CivAction[] behaviour;
	final SpecialStructureType specialStructure;

	private SentientType(String name, String desc, String specialStructName, String pSuffix, String[] nameStarts, String[] nameEnds, CivAction... behaviour) {
		this.name = name;
		this.desc = desc;
		this.behaviour = behaviour;
		this.pSuffix = pSuffix;
		this.specialStructure = new SpecialStructureType(specialStructName);
		this.nameStarts = nameStarts;
		this.nameEnds = nameEnds;
	}
	
	static class SpecialStructureType implements StructureType {
		final String name;

		public SpecialStructureType(String name) {
			this.name = name;
		}
		@Override
		public String getName() { return name; }
	}
}
