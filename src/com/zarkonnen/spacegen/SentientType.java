package com.zarkonnen.spacegen;

import java.util.ArrayList;
import static com.zarkonnen.spacegen.CivAction.*;

public class SentientType {
	static final SentientType PARASITES;
	
	static {
		PARASITES = new SentientType();
		PARASITES.base = Base.PARASITES;
		PARASITES.name = PARASITES.mkName();
		PARASITES.specialStructures.add(Base.PARASITES.specialStructure);
		PARASITES.personality = "insidious";
		PARASITES.goal = "strive to dominate all sentient life";
	}

	static SentientType invent(SpaceGen sg, Civ creator, Planet p, String specialOrigin) {
		SentientType st = null;
		do {
			st = invent2(sg, creator, p, specialOrigin);
		} while (sg.historicalSentientNames.contains(st.name));
		sg.historicalSentientNames.add(st.name);
		return st;
	}
	
	static SentientType invent2(SpaceGen sg, Civ creator, Planet p, String newSpecialOrigin) {
		SentientType st = new SentientType();
		ArrayList<Base> bss = new ArrayList<Base>();
		for (Base b : Base.values()) { if (b.evolvable) { bss.add(b); } }
		st.creators = creator;
		st.evolvedLoc = p;
		st.birth = sg.year;
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
		st.personality = sg.pick(PERSONALITY);
		st.goal = sg.pick(GOAL);
		st.name = st.mkName();
		st.specialOrigin = newSpecialOrigin;
		return st;
	}

	static SentientType genRobots(SpaceGen sg, Civ creator, Planet p, String specialOrigin) {
		SentientType st = null;
		do {
			st = genRobots2(sg, creator, p, specialOrigin);
		} while (sg.historicalSentientNames.contains(st.name));
		sg.historicalSentientNames.add(st.name);
		return st;
	}
	
	static SentientType genRobots2(SpaceGen sg, Civ creator, Planet p, String newSpecialOrigin) {
		SentientType st = new SentientType();
		st.creators = creator;
		st.evolvedLoc = p;
		st.birth = sg.year;
		st.base = Base.ROBOTS;
		st.specialStructures.add(st.base.specialStructure);
		if (sg.coin() && st.base != Base.HUMANOIDS) {
			st.color = sg.pick(Names.COLORS);
		}
		st.prefixes.add(sg.pick(Prefix.values()));
		if (st.prefixes.get(0).specialStruct != null) {
			st.specialStructures.add(st.prefixes.get(0).specialStruct);
		}
		if (sg.p(5)) {
			st.postfix = sg.pick(Postfix.values());
		}
		if (st.postfix != null && st.postfix.specialStruct != null) {
			st.specialStructures.add(st.postfix.specialStruct);
		}
		st.personality = sg.pick(PERSONALITY);
		st.goal = sg.pick(GOAL);
		st.name = st.mkName();
		st.specialOrigin = newSpecialOrigin;
		return st;
	}
	
	public SentientType mutate(SpaceGen sg, String specialOrigin) {
		SentientType st = null;
		do {
			st = mutate2(sg, specialOrigin);
		} while (sg.historicalSentientNames.contains(st.name));
		return st;
	}
	
	public SentientType mutate2(SpaceGen sg, String newSpecialOrigin) {
		SentientType st2 = new SentientType();
		st2.base = base;
		st2.prefixes.addAll(prefixes);
		st2.color = color;
		st2.postfix = postfix;
		st2.cyborg = cyborg;
		st2.personality = personality;
		st2.evolvedLoc = evolvedLoc;
		st2.creators = creators;
		st2.goal = goal;
		st2.birth = birth;
		
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
			if (sg.p(3)) {
				st2.personality = sg.pick(PERSONALITY);
			}
			if (sg.p(3)) {
				st2.goal = sg.pick(GOAL);
			}
		}
		
		st2.specialStructures.add(st2.base.specialStructure);
		if (st2.postfix != null && st2.postfix.specialStruct != null) {
			st2.specialStructures.add(st2.postfix.specialStruct);
		}
		st2.name = st2.mkName();
		if (newSpecialOrigin == null) {
			st2.specialOrigin = "They developed from " + name + " in " + sg.year + ".";
		} else {
			st2.specialOrigin = newSpecialOrigin;
		}
		return st2;
	}
	
	int birth;
	Planet evolvedLoc;
	Civ creators;
	public Base base;
	public ArrayList<StructureType> specialStructures = new ArrayList<StructureType>();
	public ArrayList<Prefix> prefixes = new ArrayList<Prefix>();
	public String color;
	public Postfix postfix;
	public boolean cyborg;
	String personality;
	String goal;
	String name;
	String specialOrigin;
	
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
	
	public static String[] PERSONALITY = {
		"cautious",
		"violent",
		"cowardly",
		"peaceful",
		"humorless",
		"hyperactive",
		"gregarious",
		"reclusive",
		"meditative",
		"fond of practical jokes",
		"modest",
		"thrill-seeking"
	};
	
	public static String[] GOAL = {
		"deeply religious",
		"value personal integrity above all else",
		"have a complex system of social castes",
		"strive to uphold their ideals of personal honour",
		"deeply rationalist",
		"unwilling to compromise",
		"always willing to listen",
		"have a deep need to conform",
		"believe in the survival of the fittest",
		"believe in the inherent superiority of their species",
		"believe that each individual must serve the community",
		"have a complex system of social rules"
	};
	
	String getDesc() {
		StringBuilder sb = new StringBuilder(base.desc);
		// Skin
		if (color != null) {
			if (prefixes.contains(Prefix.FEATHERED)) {
				sb.append(" They have ").append(color.toLowerCase()).append(" feathers.");
			} else if (prefixes.contains(Prefix.SCALY)) {
				sb.append(" They have ").append(color.toLowerCase()).append(" scales.");
			} else {
				sb.append(" They have ").append(color.toLowerCase()).append(" skin.");
			}
		}
		// LIMBS!
		if (prefixes.contains(Prefix.SIX_LEGGED)) {
			sb.append(" They have six legs.");
		}
		if (prefixes.contains(Prefix.FOUR_ARMED)) {
			sb.append(" They have four arms.");
		}
		if (prefixes.contains(Prefix.TWO_HEADED)) {
			sb.append(" They have two heads that tend to constantly bicker with one another.");
		}
		
		if (postfix == Postfix.S_3) {
			sb.append(" They have trilateral symmetry");
		} else  if (postfix == Postfix.S_5) {
			sb.append(" They have five-fold symmetry");
		} else {
			sb.append(" They have bilateral symmetry");
		}
		if (prefixes.contains(Prefix.SLIM)) {
			sb.append(" and a slim shape.");
		} else if (prefixes.contains(Prefix.AMORPHOUS)) {
			sb.append(" and are able to greatly alter their shape.");
		} else {
			sb.append(".");
		}
		if (postfix == Postfix.EYES) {
			sb.append(" Their giant eyes take up most of their head.");
		}	
		if (postfix == Postfix.TAILS) {
			sb.append(" They use their long tails for balance.");
		}	
		if (prefixes.contains(Prefix.FLYING)) {
			sb.append(" They can fly.");
		}
		if (prefixes.contains(Prefix.TELEPATHIC)) {
			sb.append(" They can read each others' minds.");
		}
		if (prefixes.contains(Prefix.IMMORTAL)) {
			sb.append(" They have no fixed lifespan and only die of disease or accidents.");
		}
		
		sb.append(" They are ").append(personality).append(" and ").append(goal).append(".");
		
		if (specialOrigin != null) {
			sb.append(" ").append(specialOrigin);
		} else {
			if (evolvedLoc != null) {
				if (base == Base.ROBOTS) {
					sb.append(" They were created by the ").append(creators.name).append(" on ").append(evolvedLoc.name).append(" as servants in ").append(birth).append(".");
				} else {
					if (creators != null) {
						sb.append(" They were uplifted by the ").append(creators.name).append(" on ").append(evolvedLoc.name).append(" in ").append(birth).append(".");
					} else {
						sb.append(" They first evolved on ").append(evolvedLoc.name).append(" in ").append(birth).append(".");
					}
				}
			}
		}
		
		return sb.toString();
	}
	
	public static enum Prefix {
		FLYING("Flying", "grand roost"),
		TINY("Tiny", null),
		GIANT("Giant", null),
		SIX_LEGGED("Six-Legged", null),
		FOUR_ARMED("Four-Armed", null),
		TWO_HEADED("Two-Headed", null),
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
		KOBOLDOIDS(
			"Koboldoids", true,
			"Eat pretty much anything, including one another. Disturbingly fond of skulls.",
			"skull pit",
			" Claw",
			new String[] { "Grzikngh", "Brghz", "Zraa", "Klutt", "Murgezzog", "Okkog Okkog", "Frix", "Zrippo", "Zazapakka", "Krull", "Blorgorz", "Uzzakk", "Hittehelmettepol", "Zong", "Krghl" },
			new String[] { " Jameson", " Smith", " Jones", " Taylor", " Brown", " Williams", " Smythe", " Clarke", " Robinson", " Wilson", " Johnson", " Walker", " Wood", " Hall", " Thompson" },
			EXPLORE_PLANET, COLONISE_PLANET, COLONISE_PLANET, COLONISE_PLANET, BUILD_MINING_BASE, BUILD_CONSTRUCTION, BUILD_CONSTRUCTION),
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
