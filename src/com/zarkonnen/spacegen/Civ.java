package com.zarkonnen.spacegen;

import java.util.ArrayList;
import java.util.HashMap;

public class Civ {	
	ArrayList<SentientType> fullMembers = new ArrayList<SentientType>();
	Government govt;
	ArrayList<Planet> colonies = new ArrayList<Planet>();
	HashMap<Civ, Diplomacy.Outcome> relations = new HashMap<Civ, Diplomacy.Outcome>();
	
	int resources = 0;
	int science = 0;
	int military = 0;
	int techLevel = 1;
	String name;
	int birthYear;
	
	public Diplomacy.Outcome relation(Civ c) {
		if (!relations.containsKey(c)) { relations.put(c, Diplomacy.Outcome.PEACE); }
		return relations.get(c);
	}

	public Civ(int year, SentientType st, Planet home, Government govt, int resources, ArrayList<Civ> historicals) {
		this.govt = govt;
		this.fullMembers.add(st);
		this.colonies.add(home);
		this.resources = resources;
		this.birthYear = year;
		home.owner = this;
		updateName(historicals);
	}
	
	public int population() {
		int sum = 0;
		for (Planet col : colonies) { sum += col.population(); }
		return sum;
	}
	
	public ArrayList<Planet> fullColonies() {
		ArrayList<Planet> fcs = new ArrayList<Planet>();
		for (Planet col : colonies) {
			if (col.population() > 0) { fcs.add(col); }
		}
		return fcs;
	}
	
	public Planet largestColony() {
		int sz = 0;
		Planet largest = null;
		for (Planet col : colonies) {
			if (col.population() > sz) { largest = col; sz = col.population(); }
		}
		return largest;
	}
		
	final String genName(int nth) {
		String n = "";
		if (nth > 1) { n = Names.nth(nth) + " "; }
		n += govt.title + " of ";
		for (int i = 0; i < fullMembers.size(); i++) {
			if (i > 0) {
				if (i == fullMembers.size() - 1) {
					n += " and ";
				} else {
					n += ", ";
				}
			}
			n += fullMembers.get(i).name;
		}
		return n;
	}

	final void updateName(ArrayList<Civ> historicals) {
		int nth = 0;
		lp: while (true) {
			nth++;
			String n = genName(nth);
			for (Civ c : historicals) {
				if (c.name.equals(n)) { continue lp; }
			}
			name = n;
			break;
		}
	}
}
