package com.zarkonnen.spacegen;

import java.util.ArrayList;
import static com.zarkonnen.spacegen.CivAction.*;

public class SentientType {
	static SentientType genParasites() {
		SentientType st = new SentientType();
		st.base = Base.PARASITES;
		st.name = st.mkName();
		st.specialStructures.add(Base.PARASITES.specialStructure);
		return st;
	}

	static SentientType invent(SpaceGen sg) {
		SentientType st = new SentientType();
		ArrayList<Base> bss = new ArrayList<Base>();
		for (Base b : Base.values()) { if (b.evolvable) { bss.add(b); } }
		st.base = sg.pick(bss);
		st.specialStructures.add(st.base.specialStructure);
		if (sg.coin() && st.base != Base.HUMANOIDS) {
			st.color = sg.pick(Names.COLORS);
		}
		if (st.color == null || sg.p(3)) {
			st.prefixes.add(sg.pick(Prefix.values()));
			if (st.prefixes.get(0).specialStruct != null) {
				st.specialStructures.add(st.prefixes.get(0).specialStruct);
			}
		}
		if (sg.p(5)) {
			st.postfix = sg.pick(Postfix.values());
		}
		if (st.postfix != null && st.postfix.specialStruct != null) {
			st.specialStructures.add(st.postfix.specialStruct);
		}
		st.name = st.mkName();
		
		return st;
	}
	
	public SentientType mutate(SpaceGen sg) {
		SentientType st2 = new SentientType();
		st2.base = base;
		st2.prefixes.addAll(prefixes);
		st2.color = color;
		st2.postfix = postfix;
		st2.cyborg = cyborg;
		
		while (st2.mkName().equals(name)) {
			switch (sg.d(3)) {
				case 0:
					if (st2.base == Base.HUMANOIDS) { continue; }
					st2.color = sg.pick(Names.COLORS);
					break;
				case 1:
					st2.prefixes.clear();
					st2.prefixes.add(sg.pick(Prefix.values()));
					break;
				case 2:
					st2.postfix = sg.pick(Postfix.values());
					break;
			}
		}
		
		st2.specialStructures.add(st2.base.specialStructure);
		if (st2.postfix != null && st2.postfix.specialStruct != null) {
			st2.specialStructures.add(st2.postfix.specialStruct);
		}
		st2.name = st2.mkName();
		return st2;
	}
	
	public Base base;
	public ArrayList<StructureType> specialStructures = new ArrayList<StructureType>();
	public ArrayList<Prefix> prefixes = new ArrayList<Prefix>();
	public String color;
	public Postfix postfix;
	public boolean cyborg;
	String name;
	
	@Override
	public boolean equals(Object o2) {
		return o2 instanceof SentientType && ((SentientType) o2).name.equals(name);
	}
	
	@Override
	public int hashCode() { return name.hashCode(); }
	
	String getName() { return name; }
	
	String mkName() {
		StringBuilder sb = new StringBuilder();
		for (Prefix pf : prefixes) {
			sb.append(pf.name).append(" ");
		}
		if (color != null) {
			sb.append(color).append(" ");
		}
		if (cyborg) { sb.append("Cyborg "); }
		sb.append(base.name);
		if (postfix != null) {
			sb.append(" ").append(postfix.name);
		}
		return sb.toString();
	}
	
	String getDesc() {
		return getName() + ": " + base.desc;
	}
	
	public static enum Prefix {
		FLYING("Flying", "grand roost"),
		TINY("Tiny", null),
		GIANT("Giant", null),
		SIX_LEGGED("Six-Legged", "velodrome"),
		FOUR_ARMED("Four-Armed", null),
		TWO_HEADED("Two-Headed", null),
		HERBIVOROUS("Herbivorous", null),
		SLIM("Slim", null),
		AMORPHOUS("Amorphous", "reforming vat"),
		FEATHERED("Feathered", null),
		SCALY("Scaly", null),
		IMMORTAL("Immortal", null),
		TELEPATHIC("Telepathic", null);
		
		final String name;
		final StructureType specialStruct;

		private Prefix(String name, String ssName) {
			this.name = name;
			specialStruct = ssName == null ? null : new SpecialStructureType(ssName);
		}
	}
	
	public static enum Postfix {
		S_5("with Five-fold Symmetry", null),
		S_3("with Threefold Symmetry", null),
		EYES("with Giant Eyes", null),
		TAILS("with Long Tails", null);
		
		final String name;
		final StructureType specialStruct;

		private Postfix(String name, String ssName) {
			this.name = name;
			specialStruct = ssName == null ? null : new SpecialStructureType(ssName);
		}
	}
	
	public static enum Base {
		ANTOIDS(
			"Antoids", true,
			"Small, industrious and well-organised, these creatures' greatest weakness is their ever-ballooning population.",
			"cluster of breeding pits",
			" Antenna",
			new String[] { "Kak", "Krk'", "Tk", "Tch'", "Tk'k" },
			new String[] { "erlak", "kra", "hkt", "ukk", "kraa" },
			EXPLORE_PLANET, COLONISE_PLANET, COLONISE_PLANET, COLONISE_PLANET, BUILD_MINING_BASE, BUILD_MINING_BASE, BUILD_CONSTRUCTION),
		URSOIDS(
			"Ursoids", true,
			"Always at war with someone, or everyone, or one another.",
			"barracks",
			"muzzle",
			new String[] { "Ur", "Ber'", "Gro", "Brm'", "Or" },
			new String[] { "sus", "mog", "rr", "orr", "serk" },
			EXPLORE_PLANET, EXPLORE_PLANET, BUILD_MILITARY_BASE, BUILD_WARSHIPS, BUILD_WARSHIPS),
		DWARFOIDS(
			"Dwarfoids", true,
			"Fond of drink and industry - especially building strange contraptions.",
			"great hall",
			"beard",
			new String[] { "Urist", "Grolin", "Gnolin", "Minin", "Balin" },
			new String[] { " McUrist", " Thundersson", " Longbeard", " Bronzepick", " Carpwrestler" },
			EXPLORE_PLANET, COLONISE_PLANET, BUILD_MINING_BASE, BUILD_MINING_BASE, BUILD_MINING_BASE, DO_RESEARCH, BUILD_CONSTRUCTION),
		CATOIDS(
			"Catoids", true,
			"Always curious and often cruel.",
			"torture chamber",
			"paw",
			new String[] { "Mrr", "Mrw", "Mmbrr", "Rrre", "Mee" },
			new String[] { "oaw", "ow", "orr", "reww", "ar" },
			EXPLORE_PLANET, COLONISE_PLANET, EXPLORE_PLANET, EXPLORE_PLANET, BUILD_WARSHIPS, BUILD_WARSHIPS),
		TROLLOIDS(
			"Trolloids", true,
			"They have lost the ability to reproduce naturally, and compensate for this with a focus on science and paranoia.",
			"gene library",
			"rock",
			new String[] { "Unkut", "Rufkut", "Finkut"  },
			new String[] { " Sedimin", " Aa", " Igneous", " Geode" },
			EXPLORE_PLANET, BUILD_MILITARY_BASE, BUILD_MILITARY_BASE, BUILD_SCIENCE_OUTPOST, BUILD_SCIENCE_OUTPOST, DO_RESEARCH),
		DEEP_DWELLERS(
			"Deep Dwellers", true,
			"They live in the deep oceans or in deep caves, hiding. Their presence can go unnoticed for a long time.",
			"deep dome",
			"crest",
			new String[] { "Zursu", "Uln", "Wi", "Paraa", "Nio" },
			new String[] { "pram", "ivex", "lon", "ix", "it" },
			BUILD_SCIENCE_OUTPOST, BUILD_MILITARY_BASE, BUILD_MILITARY_BASE, BUILD_MINING_BASE, BUILD_MINING_BASE, COLONISE_PLANET),
		PARASITES(
			"Parasites", false,
			"Operate by infesting the brains of other sentients and using their bodies as vehicles.",
			"biolab",
			" Tentacle",
			new String[] { "Dark ", "Shining ", "Slithering ", "Grasping ", "Insidious " },
			new String[] { "Tentacle", "Tentril", "Beak", "Ovipositor", "Needle" },
			EXPLORE_PLANET, EXPLORE_PLANET, EXPLORE_PLANET, COLONISE_PLANET, COLONISE_PLANET, DO_RESEARCH),
		ROBOTS(
			"Robots", false,
			"Purely mechanical lifeforms, artificially created.",
			"repair bay",
			" Node",
			new String[] { "Node ", "Subroutine ", "Subcomponent ", "Unit " },
			new String[] { "23/4432", "12-Theta-23", "039", "550-b", "12-a/x" },
			BUILD_WARSHIPS, BUILD_MINING_BASE, BUILD_CONSTRUCTION, BUILD_CONSTRUCTION, BUILD_CONSTRUCTION, BUILD_CONSTRUCTION),
		HUMANOIDS(
			"Humanoids", true,
			"Pretty adventurous but otherwise fairly average.",
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
		final boolean evolvable;
		final SpecialStructureType specialStructure;

		private Base(String name, boolean evolvable, String desc, String specialStructName, String pSuffix, String[] nameStarts, String[] nameEnds, CivAction... behaviour) {
			this.name = name;
			this.desc = desc;
			this.behaviour = behaviour;
			this.pSuffix = pSuffix;
			this.specialStructure = new SpecialStructureType(specialStructName);
			this.nameStarts = nameStarts;
			this.nameEnds = nameEnds;
			this.evolvable = evolvable;
		}

		
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
