package com.zarkonnen.spacegen;

import com.zarkonnen.spacegen.ArtefactType.Device;
import com.zarkonnen.spacegen.SentientType.Base;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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
	int decrepitude = 0;
	
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

	public Civ(int year, SentientType st, Planet home, Government govt, int resources, ArrayList<String> historicals) {
		this.govt = govt;
		if (st != null) {
			this.fullMembers.add(st);
		}
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
		if (fullMembers.size() == 1) {
			n += fullMembers.get(0).getName();
		} else {
			HashSet<SentientType.Base> bases = new HashSet<SentientType.Base>();
			for (SentientType st : fullMembers) { bases.add(st.base); }
			ArrayList<SentientType.Base> bs = new ArrayList<SentientType.Base>(bases);
			for (int i = 0; i < bs.size(); i++) {
				if (i > 0) {
					if (i == bs.size() - 1) {
						n += " and ";
					} else {
						n += ", ";
					}
				}
				n += bs.get(i).name;
			}
		}
		return n;
	}

	final void updateName(ArrayList<String> historicals) {
		int nth = 0;
		while (true) {
			nth++;
			String n = genName(nth);
			if (historicals.contains(n)) { continue; }
			name = n;
			break;
		}
	}
	
	public String fullDesc(SpaceGen sg) {
		StringBuilder sb = new StringBuilder();
		sb.append("THE ").append(name.toUpperCase()).append(":\n");
		int age = sg.year - birthYear;
		if (age < 3) {
			sb.append("A recently emerged");
		} else if (age < 8) {
			sb.append("A young");
		} else if (age < 16) {
			sb.append("A well-established");
		} else {
			sb.append("An ancient");
		}
		
		if (decrepitude >= 20) {
			sb.append(", corrupt");
		} else if (decrepitude >= 40) {
			sb.append(", crumbling");
		}
		
		if (resources < 2) {
			sb.append(", dirt poor");
		} else if (resources < 4) {
			sb.append(", impoverished");
		} else if (resources < 16) {
			
		} else if (resources < 25) {
			sb.append(", wealthy");
		} else {
			sb.append(", fantastically wealthy");
		}
		
		if (techLevel < 2) {
			sb.append(", primitive");
		} else if (techLevel < 4) {
			
		} else if (techLevel < 7) {
			sb.append(", advanced");
		} else {
			sb.append(", highly advanced");
		}
		
		sb.append(" ").append(govt.title).append(" of ");
		if (colonies.size() == 1) {
			sb.append("a single planet, ").append(colonies.get(0).name);
		} else {
			sb.append(colonies.size()).append(" planets");
		}
		sb.append(", with ").append(population()).append(" billion inhabitants.\n");
		sb.append("Major populations:\n");
		HashMap<SentientType, Integer> pops = new HashMap<SentientType, Integer>();
		for (Planet c : colonies) { for (Population pop : c.inhabitants) {
			if (!pops.containsKey(pop.type)) {
				pops.put(pop.type, pop.size);
			} else {
				pops.put(pop.type, pops.get(pop.type) + pop.size);
			}
		}}
		for (Map.Entry<SentientType, Integer> e : pops.entrySet()) {
			if (!fullMembers.contains(e.getKey())) { continue; }
			sb.append(e.getValue()).append(" billion ").append(e.getKey().getName()).append(".\n");
		}
		for (Map.Entry<SentientType, Integer> e : pops.entrySet()) {
			if (fullMembers.contains(e.getKey())) { continue; }
			sb.append(e.getValue()).append(" billion enslaved ").append(e.getKey().getName()).append(".\n");
		}
		HashSet<Device> devices = new HashSet<Device>();
		for (Planet c : colonies) { for (Artefact a : c.artefacts) {
			if (a.type instanceof Device) {
				devices.add((Device) a.type);
			}
		}}
		for (Device d : devices) {
			sb.append("It controls a ").append(d.getName()).append(".\n");
		}
		for (Civ other : sg.civs) {
			if (other == this) { continue; }
			if (relation(other) == Diplomacy.Outcome.WAR) {
				sb.append("It is at war with the ").append(other.name).append(".\n");
			} else {
				sb.append("It is at peace with the ").append(other.name).append(".\n");
			}
		}
		
		return sb.toString();
	}

	boolean has(Base base) {
		for (SentientType st : fullMembers) { if (st.base == base) { return true; } }
		return false;
	}
}
