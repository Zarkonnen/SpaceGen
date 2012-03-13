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

import java.util.ArrayList;
import java.util.Collections;

import static com.zarkonnen.spacegen.Main.*;

public enum BadCivEvent {
	REVOLT() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			if (actor.fullColonies().size() < 2) { return; }
			for (Planet col : actor.fullColonies()) {
				ArrayList<Population> rebels = new ArrayList<Population>();
				int nRebels = 0;
				for (Population pop : col.inhabitants) {
					if (!actor.fullMembers.contains(pop.type)) {
						rebels.add(pop);
						nRebels += pop.getSize();
					}
				}	
				if (nRebels > col.population() / 2) {
					if (actor.has(ArtefactType.Device.MIND_CONTROL_DEVICE)) {
						rep.append("A slave revolt on ").append(col.name).append(" is quickly suppressed using the mind control device of the ").append(actor.name).append(".");
						return;
					}
					if (actor.has(ArtefactType.Device.VIRTUAL_REALITY_MATRIX)) {
						rep.append("A slave revolt on ").append(col.name).append(" fizzles out when the virtual reality matrix of the ").append(actor.name).append(" is adjusted.");
						return;
					}
					if (actor.has(ArtefactType.Device.PLANET_DESTROYER)) {
						rep.append("A slave revolt on ").append(col.name).append(" falters from fear of the planet destroyer wielded by the ").append(actor.name).append(".");
						return;
					}
					if (col.has(SentientType.Base.CATOIDS.specialStructure)) {
						rep.append("A slave revolt on ").append(col.name).append(" falters from fear of torture pits of the ").append(actor.name).append(".");
						return;
					}
					int resTaken = actor.getResources() / actor.colonies.size();
					int milTaken = actor.getMilitary() / actor.colonies.size();
					Civ newCiv = new Civ(sg.year, rebels.get(0).type, col, Government.REPUBLIC, resTaken, sg.historicalCivNames);
					actor.colonies.remove(col);
					actor.setResources(actor.getResources() - resTaken);
					actor.setMilitary(actor.getMilitary() - milTaken);
					newCiv.setMilitary(milTaken);
					newCiv.relations.put(actor, Diplomacy.Outcome.WAR);
					col.setOwner(newCiv);
					actor.relations.put(newCiv, Diplomacy.Outcome.WAR);
					for (Population pop : new ArrayList<Population>(col.inhabitants)) {
						if (!rebels.contains(pop)) {
							col.dePop(pop, sg.year, null, "during a slave revolt", null);
						}
					}
					sg.civs.add(newCiv);
					sg.historicalCivNames.add(newCiv.name);
					for (Planet p : newCiv.colonies) { for (Population pop : p.inhabitants) { pop.addUpdateImgs(); } }
					animate();
					rep.append("Slaves on ").append(col.name).append(" revolt, killing their oppressors and declaring the Free ").append(newCiv.name).append(".");
					return;
				}
			}
		}
	},
	THEFT() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			ArrayList<Artefact> cands = new ArrayList<Artefact>();
			for (Planet p : actor.colonies) {
				cands.addAll(p.artefacts);
			}
			if (cands.isEmpty()) { return; }
			Artefact a = sg.pick(cands);
			Planet p = null;
			for (Planet p2 : actor.colonies) { if (p2.artefacts.contains(a)) { p = p2; } }
			Planet newP = sg.pick(sg.planets);
			//p.artefacts.remove(a);
			// qqDPS
			p.removeArtefact(a);
			newP.strata.add(new LostArtefact("hidden", sg.year, a));
			rep.append("The ").append(a).append(" on ").append(p.name).append(" has been stolen and hidden on ").append(newP.name).append(".");
		}
	},
	PUTSCH() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			if (actor.getGovt() == Government.DICTATORSHIP) {
				return;
			}
			SentientType rulers = sg.pick(actor.fullMembers);
			String oldName = actor.name;
			actor.fullMembers.clear();
			actor.fullMembers.add(rulers);
			actor.setGovt(Government.DICTATORSHIP, sg.historicalCivNames);
			sg.historicalCivNames.add(actor.name);
			for (Planet p : actor.colonies) { for (Population pop : p.inhabitants) { pop.addUpdateImgs(); } }
			animate();
			rep.append("A military putsch turns the ").append(oldName).append(" into the ").append(actor.name).append(".");
		}
	},
	RELIGIOUS_REVIVAL() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			String oldName = actor.name;
			actor.setGovt(Government.THEOCRACY, sg.historicalCivNames);
			sg.historicalCivNames.add(actor.name);
			for (Planet p : actor.colonies) { for (Population pop : p.inhabitants) { pop.addUpdateImgs(); } }
			animate();
			rep.append("Religious fanatics sieze power in the ").append(oldName).append(" and declare the ").append(actor.name).append(".");
		}
	},
	MARKET_CRASH() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			actor.setResources(actor.getResources() / 5);
			rep.append("A market crash impoverishes the ").append(actor.name).append(".");
		}
	},
	DARK_AGE() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			rep.append("The ").append(actor.name).append(" enters a dark age.");
			actor.setTechLevel(actor.getTechLevel() - 1);
			if (actor.getTechLevel() == 0) {
				if (actor.fullMembers.size() > 1) {
					rep.append(" With the knowledge of faster-than-light travel lost, each planet in the empire has to fend for itself.");
				}
				for (Planet c : new ArrayList<Planet>(actor.colonies)) {
					c.darkAge(sg.year);
				}
				actor.colonies.clear();
			}
		}
	},
	MASS_HYSTERIA() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			rep.append("Mass hysteria breaks out in the ").append(actor.name).append(", killing billions.");
			for (Planet c : actor.fullColonies()) {
				int pop = c.population();
				c.inhabitants.get(0).setSize(1);
				if (pop > 1 && pop > 3) {
					while (c.inhabitants.size() > 1) {
						c.dePop(c.inhabitants.get(1), sg.year, null, "from mass hysteria", null);
					}
				}
			}
		}
	},
	ACCIDENT() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			Planet p = sg.pick(actor.fullColonies());
			rep.append("An industrial accident on ").append(p.name).append(" causes deadly levels of pollution.");
			p.setPollution(p.getPollution() + 5);
		}
	},
	CIVIL_WAR() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			ArrayList<Planet> bigPlanets = new ArrayList<Planet>();
			for (Planet c : actor.colonies) {
				if (c.population() > 2) { bigPlanets.add(c); }
			}
			if (bigPlanets.size() > 1) {
				Collections.shuffle(bigPlanets, sg.r);
				Civ newCiv = new Civ(sg.year, null, bigPlanets.get(0), sg.pick(Government.values()), actor.getResources() / 2, sg.historicalCivNames);
				newCiv.fullMembers.clear();
				newCiv.setMilitary(actor.getMilitary() / 2);
				actor.setMilitary(actor.getMilitary() - newCiv.getMilitary());
				actor.setResources(actor.getResources() - newCiv.getResources());
				for (int i = 1; i < bigPlanets.size() / 2; i++) {
					newCiv.colonies.add(bigPlanets.get(i));
					bigPlanets.get(i).setOwner(newCiv);
					for (Population pop : bigPlanets.get(i).inhabitants) {
						if (!newCiv.fullMembers.contains(pop.type)) {
							newCiv.fullMembers.add(pop.type);
						}
					}
				}
				for (Population pop : bigPlanets.get(0).inhabitants) {
					if (!newCiv.fullMembers.contains(pop.type)) {
						newCiv.fullMembers.add(pop.type);
					}
				}
				for (Planet c : new ArrayList<Planet>(actor.colonies)) {
					if (bigPlanets.contains(c)) { continue; }
					if (sg.coin()) {
						newCiv.colonies.add(c);
						c.setOwner(newCiv);
					}
				}
				actor.colonies.removeAll(newCiv.colonies);
				newCiv.setGovt(newCiv.getGovt(), sg.historicalCivNames);
				newCiv.relations.put(actor, Diplomacy.Outcome.WAR);
				actor.relations.put(newCiv, Diplomacy.Outcome.WAR);
				sg.civs.add(newCiv);
				sg.historicalCivNames.add(newCiv.name);
				for (Planet p : newCiv.colonies) { for (Population pop : p.inhabitants) { pop.addUpdateImgs(); } }
				animate();
				rep.append("The ").append(newCiv.name).append(" secedes from the ").append(actor.name).append(", leading to a civil war!");
			}
		}
	},
	PLAGUE() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			if (actor.fullColonies().isEmpty()) { return; }
			Planet p = sg.pick(actor.fullColonies());
			Plague plague = new Plague(sg);
			ArrayList<Population> is = new ArrayList<Population>();
			for (Population pop : p.inhabitants) {
				if (pop.type.base != SentientType.Base.ROBOTS) {
					is.add(pop);
				}
			}
			if (is.isEmpty()) { return; }
			plague.affects.add(sg.pick(is).type);
			p.addPlague(plague);
			rep.append("The deadly ").append(plague.desc()).append(", arises on ").append(p.name).append(".");
		}
	},
	STARVATION() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			if (actor.has(ArtefactType.Device.UNIVERSAL_NUTRIENT)) {
				return;
			}
			Planet p = sg.pick(actor.fullColonies());
			int deaths = 0;
			for (Population pop : new ArrayList<Population>(p.inhabitants)) {
				if (pop.type.base == SentientType.Base.ROBOTS) { continue; }
				int d = pop.getSize() - pop.getSize() / 2;
				
				if (d >= pop.getSize()) {
					p.dePop(pop, sg.year, null, "due to starvation", null);
					deaths += pop.getSize();
				} else {
					pop.setSize(pop.getSize() - d);
					deaths += d;
				}
			}
			if (deaths == 0) { return; }
			rep.append("A famine breaks out on ").append(p.name).append(", killing ").append(deaths).append(" billion");
			if (p.population() == 0) {
				rep.append(", wiping out all sentient life.");
				p.deCiv(sg.year, null, "due to starvation");
			} else {
				rep.append(".");
			}
		}
	},
	SPAWN_PIRATE() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			SentientType st = sg.pick(actor.fullMembers);
			String color = sg.pick(Names.COLORS);
			String name = color + st.base.pSuffix;
			Planet p = sg.pick(actor.colonies);
			rep.append("The pirate ").append(name).append(" establishes ").append(sg.coin() ? "himself" : "herself").append(" on ").append(p.name).append(".");
			Agent ag = new Agent(AgentType.PIRATE, sg.year, name, sg);
			ag.color = color;
			ag.fleet = 2 + sg.d(6);
			ag.resources = sg.d(6);
			ag.originator = actor;
			ag.st = st;
			sg.agents.add(ag);
			confirm();
		}
	},
	ROGUE_AI() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			Planet p = sg.pick(actor.colonies);
			String pref = sg.pick(new String[] { "Experiment ", "System ", "Mind ", "Simulation " });
			Agent ag = new Agent(AgentType.ROGUE_AI, sg.year, pref + sg.r.nextInt(500), sg);
			ag.setLocation(p);
			rep.append("The ").append(actor.name).append(" accidentally create the rogue AI ").append(ag.name).append(" on ").append(p.name).append(".");
			ag.originator = actor;
			sg.agents.add(ag);
			confirm();
		}
	},
	;
	// SPAWN_ADVENTURER
	// SPAWN_PRIVATEER
	
	public void i(Civ actor, SpaceGen sg, StringBuilder rep) { return; }
	public void invoke(Civ actor, SpaceGen sg) {
		StringBuilder rep = new StringBuilder();
		i(actor, sg, rep);
		if (rep.length() > 0) {
			sg.l(rep.toString());
			confirm();
		}
	}
}
