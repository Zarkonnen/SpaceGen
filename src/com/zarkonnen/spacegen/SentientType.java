package com.zarkonnen.spacegen;

import static com.zarkonnen.spacegen.CivAction.*;

public enum SentientType {
	ANTOIDS(
		"Antoids",
		"Small, industrious and well-organised, these creatures' greatest weakness is their ever-ballooning population",
		EXPLORE_PLANET, COLONISE_PLANET, COLONISE_PLANET, COLONISE_PLANET, BUILD_MINING_BASE, BUILD_MINING_BASE, BUILD_CONSTRUCTION),
	URSOIDS(
		"Ursoids",
		"Ursoids are always at war with someone, or everyone, or one another.",
		EXPLORE_PLANET, EXPLORE_PLANET, BUILD_MILITARY_BASE, BUILD_WARSHIPS, BUILD_WARSHIPS),
	DWARFOIDS(
		"Dwarfoids",
		"Dwarfoids are fond of drink and industry - especially building strange contraptions.",
		EXPLORE_PLANET, COLONISE_PLANET, BUILD_MINING_BASE, BUILD_MINING_BASE, BUILD_MINING_BASE, DO_RESEARCH, BUILD_CONSTRUCTION),
	CATOIDS(
		"Catoids",
		"Catoids are always curious.",
		EXPLORE_PLANET, COLONISE_PLANET, EXPLORE_PLANET, EXPLORE_PLANET, BUILD_WARSHIPS, BUILD_WARSHIPS),
	TROLLOIDS(
		"Trolloids",
		"Trolloids have lost the ability to reproduce naturally, and compensate for this with a focus on science and paranoia.",
		EXPLORE_PLANET, BUILD_MILITARY_BASE, BUILD_MILITARY_BASE, BUILD_SCIENCE_OUTPOST, BUILD_SCIENCE_OUTPOST, DO_RESEARCH),
	DEEP_DWELLERS(
		"Deep Dwellers",
		"Deep Dwellers live in the deep oceans or in deep caves, hiding. Their presence can go unnoticed for a long time.",
		BUILD_SCIENCE_OUTPOST, BUILD_MILITARY_BASE, BUILD_MILITARY_BASE, BUILD_MINING_BASE, BUILD_MINING_BASE, COLONISE_PLANET),
	PARASITES(
		"Parasites",
		"Parasites operate by infesting the brains of other sentients and using their bodies as vehicles.",
		EXPLORE_PLANET, EXPLORE_PLANET, EXPLORE_PLANET, EXPLORE_PLANET, COLONISE_PLANET, DO_RESEARCH),
	TERRANS(
		"Terrans",
		"Terrans are pretty adventurous but otherwise fairly average.",
		EXPLORE_PLANET, EXPLORE_PLANET, COLONISE_PLANET, BUILD_SCIENCE_OUTPOST, BUILD_MILITARY_BASE, BUILD_MINING_BASE, BUILD_WARSHIPS)
		;
	
	final String name;
	final String desc;
	final CivAction[] behaviour;

	private SentientType(String name, String desc, CivAction... behaviour) {
		this.name = name;
		this.desc = desc;
		this.behaviour = behaviour;
	}
}
