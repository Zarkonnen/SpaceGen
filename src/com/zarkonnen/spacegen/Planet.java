package com.zarkonnen.spacegen;

import java.util.ArrayList;
import java.util.Random;

public class Planet {
	public final String name;
	
	int pollution;
	boolean habitable;
	int evoPoints;
	int evoNeeded;
	ArrayList<PlanetSpecial> specials = new ArrayList<PlanetSpecial>();
	ArrayList<SpecialLifeform> lifeforms = new ArrayList<SpecialLifeform>();
	ArrayList<Population> inhabitants = new ArrayList<Population>();
	Civ owner;
	ArrayList<Structure> structures = new ArrayList<Structure>();
	
	ArrayList<Stratum> strata = new ArrayList<Stratum>();
	
	public void dePop(Population pop, int time, Cataclysm cat, String reason) {
		strata.add(new Remnant(pop, time, cat, reason));
		inhabitants.remove(pop);
	}
	
	public void deCiv(int time, Cataclysm cat, String reason) {
		if (owner == null) { return; }
		owner.colonies.remove(this);
		owner = null;
		for (Population p : new ArrayList<Population>(inhabitants)) {
			dePop(p, time, cat, reason);
		}
		for (Structure s : structures) {
			strata.add(new Ruin(s, time, cat));
		}
		structures.clear();
	}
	
	public void deLive(int time, Cataclysm cat, String reason) {
		deCiv(time, cat, reason);
		evoPoints = 0;
		for (SpecialLifeform slf : lifeforms) {
			strata.add(new Fossil(slf, time, cat));
		}
		lifeforms.clear();
		habitable = false;
	}
	
	public Planet(Random r) {
		this.name = getName(Math.abs(r.nextInt()));
		this.evoNeeded = 12000 + (r.nextInt(3) == 0 ? 0 : 10000000);
	}
	
	public boolean has(StructureType st) {
		for (Structure s : structures) {
			if (s.type == st) { return true; }
		}
		return false;
	}
	
	static String getName(int p) {
		return new String(new char[] {
			(char) ('A' + (p + 5) % 7),
			new char[] {'u','e','y','o','i'}[(p + 2) % 5],
			(char) ('k' + (p / 3) % 4),
			new char[] {'u','e','i','o','a'}[(p / 2 + 1) % 5],
			(char) ('p' + (p / 2) % 9)
		}) + new String[] { " I", " II", " III", " IV", " V", " VI" }[(p / 4 + 3) % 6];
	}
	
	boolean isOutpost() {
		return	has(StructureType.MILITARY_BASE) ||
				has(StructureType.SCIENCE_LAB) ||
				has(StructureType.MINING_BASE);
	}

	int population() {
		int sum = 0;
		for (Population p : inhabitants) {
			sum += p.size;
		}
		return sum;
	}
	
	
	public String fullDesc() {
		StringBuilder sb = new StringBuilder(name.toUpperCase() + "\n");
		sb.append("A ").append(habitable ? "life-bearing " : "barren ").append("planet");
		if (owner != null) {
			sb.append(" of the ").append(owner.name);
		}
		sb.append(".\n");
		if (pollution > 0) {
			sb.append("It is ");
			switch (pollution) {
				case 1: sb.append("a little"); break;
				case 2: sb.append("slightly"); break;
				case 3: sb.append("somewhat"); break;
				case 4: sb.append("heavily"); break;
				case 5: sb.append("very heavily"); break;
				default: sb.append("incredibly"); break;
			}
			sb.append(" polluted.\n");
		}
		if (inhabitants.size() > 0) {
			sb.append("It is populated by:\n");
			for (Population p : inhabitants) {
				sb.append(p).append("\n");
			}
		}
		for (Structure s : structures) {
			sb.append("A ").append(s).append("\n");
		}
		for (PlanetSpecial ps : specials) {
			sb.append(ps.explanation).append("\n");
		}
		if (!lifeforms.isEmpty()) {
			sb.append("Lifeforms of note:\n");
		}
		for (SpecialLifeform ps : lifeforms) {
			sb.append(ps.name).append(": ").append(ps.desc).append("\n");
		}
		if (!strata.isEmpty()) {
			sb.append("Strata:\n");
			for (int i = strata.size() - 1; i >= 0; i--) {
				sb.append(strata.get(i)).append("\n");
			}
		}
		
		return sb.toString();
	}
}
