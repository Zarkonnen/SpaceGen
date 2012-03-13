/**
Copyright 2012 David Stark

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.zarkonnen.spacegen;

import com.zarkonnen.spacegen.ArtefactType.Device;
import com.zarkonnen.spacegen.SentientType.Base;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.zarkonnen.spacegen.Stage.*;
import static com.zarkonnen.spacegen.Main.*;

public class Civ {	
	ArrayList<SentientType> fullMembers = new ArrayList<SentientType>();
	private Government govt;
	ArrayList<Planet> colonies = new ArrayList<Planet>();
	HashMap<Civ, Diplomacy.Outcome> relations = new HashMap<Civ, Diplomacy.Outcome>();
	int number = 0;
	
	ArrayList<CivSprite> sprites = new ArrayList<CivSprite>();
	
	private int resources = 0;
	private int science = 0;
	private int military = 0;
	private int weapLevel = 0;
	private int techLevel = 0;
	String name;
	int birthYear;
	int nextBreakthrough = 6;
	int decrepitude = 0;
	
	public int getResources() {
		return resources;
	}

	public final void setResources(int resources) {
		resources = Math.max(0, resources);
		int oldRes = this.resources;
		this.resources = resources;
		for (CivSprite cs : sprites) {
			cs.changeRes(oldRes, resources);
		}
		animate();
	}
	
	public int getScience() {
		return science;
	}

	public void setScience(int science) {
		science = Math.max(0, science);
		int oldSci = this.science;
		this.science = science;
		for (CivSprite cs : sprites) {
			cs.changeScience(oldSci, science);
		}
		animate();
	}

	public int getMilitary() {
		return military;
	}

	public void setMilitary(int military) {
		military = Math.max(0, military);
		int oldMil = this.military;
		this.military = military;
		for (CivSprite cs : sprites) {
			cs.changeFleet(oldMil, military);
		}
		animate();
	}

	public int getTechLevel() {
		return techLevel;
	}

	public final void setTechLevel(int techLevel) {
		techLevel = Math.max(0, techLevel);
		this.techLevel = techLevel;
		for (CivSprite cs : sprites) {
			cs.changeTech(techLevel);
		}
		animate();
	}
	
	public int getWeapLevel() {
		return weapLevel;
	}

	public void setWeapLevel(int weapLevel) {
		weapLevel = Math.max(0, weapLevel);
		this.weapLevel = weapLevel;
		for (CivSprite cs : sprites) {
			cs.changeMilTech(weapLevel);
		}
		animate();
	}
	
	Planet leastPopulousFullColony() {
		Planet c = null;
		int pop = 0;
		for (Planet p : fullColonies()) {
			if (c == null || p.population() < pop) {
				c = p;
				pop = p.population();
			}
		}
		return c;
	}
	
	public Planet closestColony(Planet p) {
		Planet c = null;
		int closestDist = 0;
		for (Planet col : fullColonies()) {
			int dist = (p.x - col.x) * (p.x - col.x) + (p.y - col.y) * (p.y - col.y);
			if (dist < closestDist || c == null) {
				c = col;
				closestDist = dist;
			}
		}
		if (c == null) {
			return colonies.get(0);
		} else {
			return c;
		}
	}
	
	public ArrayList<Planet> reachables(SpaceGen sg) {
		int range = 3 + getTechLevel() * getTechLevel();
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
		setResources(resources);
		this.birthYear = year;
		updateName(historicals);
		home.setOwner(this);
		setTechLevel(1);
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
		int sz = -1;
		Planet largest = null;
		for (Planet col : colonies) {
			if (col.population() > sz) { largest = col; sz = col.population(); }
		}
		return largest;
	}
		
	final String genName(int nth) {
		String n = "";
		if (nth > 1) { n = Names.nth(nth) + " "; }
		n += getGovt().title + " of ";
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
		number = 0;
		while (true) {
			number++;
			String n = genName(number);
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
		
		if (getResources() < 2) {
			sb.append(", dirt poor");
		} else if (getResources() < 4) {
			sb.append(", impoverished");
		} else if (getResources() < 16) {
			
		} else if (getResources() < 25) {
			sb.append(", wealthy");
		} else {
			sb.append(", fantastically wealthy");
		}
		
		if (getTechLevel() < 2) {
			sb.append(", primitive");
		} else if (getTechLevel() < 4) {
			
		} else if (getTechLevel() < 7) {
			sb.append(", advanced");
		} else {
			sb.append(", highly advanced");
		}
		
		sb.append(" ").append(getGovt().title).append(" of ");
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
				pops.put(pop.type, pop.getSize());
			} else {
				pops.put(pop.type, pops.get(pop.type) + pop.getSize());
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

	public Government getGovt() {
		return govt;
	}

	void setGovt(Government govt, ArrayList<String> historicalNames) {
		this.govt = govt;
		updateName(historicalNames);
		animate(tracking(largestColony().sprite, delay()));
		for (CivSprite s : sprites) { add(change(s, Imager.get(this))); }
		animate();
	}
}
