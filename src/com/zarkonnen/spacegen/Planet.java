package com.zarkonnen.spacegen;

import java.util.ArrayList;
import java.util.Random;

public class Planet {
	public final String name;
	
	int pollution;
	boolean habitable;
	int evoPoints;
	ArrayList<PlanetSpecial> specials = new ArrayList<PlanetSpecial>();
	ArrayList<SpecialLifeform> lifeforms = new ArrayList<SpecialLifeform>();
	ArrayList<Population> inhabitants = new ArrayList<Population>();
	Civ owner;
	ArrayList<Structure> structures = new ArrayList<Structure>();
	
	public Planet(Random r) {
		this.name = getName(Math.abs(r.nextInt()));
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
		return	structures.contains(Structure.MILITARY_BASE) ||
				structures.contains(Structure.SCIENCE_LABS) ||
				structures.contains(Structure.MINING_BASE);
	}

	int population() {
		int sum = 0;
		for (Population p : inhabitants) {
			sum += p.size;
		}
		return sum;
	}
}
