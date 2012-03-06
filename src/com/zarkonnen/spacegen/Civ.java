package com.zarkonnen.spacegen;

import com.zarkonnen.spacegen.ArtefactType.Device;
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
	int weapLevel = 0;
	int techLevel = 1;
	String name;
	int birthYear;
	int nextBreakthrough = 6;
	
	public ArrayList<Planet> reachables(SpaceGen sg) {
		int range = 3 + techLevel * techLevel;
		if (has(ArtefactType.Device.TELEPORT_GATE)) { range = 10000; }
		ArrayList<Planet> ir = new ArrayList<Planet>();
		for (Planet p : sg.planets) {
			int closestR = 100000;
			for (Planet c : colonies) {
				int dist = (p.x - c.x) * (p.x - c.x) + (p.y - c.y) * (p.y - c.y);
				closestR = Math.min(dist, closestR);
			}
			if (closestR <= range) {
				ir.add(p);
			}
		}
		return ir;
	}
	
	public boolean has(ArtefactType at) {
		for (Planet c : colonies) {
			for (Artefact a : c.artefacts) {
				if (a.type == at) { return true; }
			}
		}
		return false;
	}
	
	Artefact use(ArtefactType at) {
		for (Planet c : colonies) {
			for (Artefact a : c.artefacts) {
				if (a.type == at) {
					c.artefacts.remove(a);
					return a;
				}
			}
		}
		return new Artefact(13, this, at, "mysterious " + at.getName() + "");
	}
	
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
