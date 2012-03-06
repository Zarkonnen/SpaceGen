package com.zarkonnen.spacegen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

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
						nRebels += pop.size;
					}
				}	
				if (nRebels > col.population() / 2) {
					int resTaken = actor.resources / actor.colonies.size();
					int milTaken = actor.military / actor.colonies.size();
					Civ newCiv = new Civ(sg.year, rebels.get(0).type, col, Government.REPUBLIC, resTaken, sg.historicalCivs);
					actor.colonies.remove(col);
					actor.resources -= resTaken;
					actor.military -= milTaken;
					newCiv.military = milTaken;
					newCiv.relations.put(actor, Diplomacy.Outcome.WAR);
					col.owner = newCiv;
					actor.relations.put(newCiv, Diplomacy.Outcome.WAR);
					for (Population pop : new ArrayList<Population>(col.inhabitants)) {
						if (!rebels.contains(pop)) {
							col.dePop(pop, sg.year, null, "during a slave revolt", null);
						}
					}
					sg.civs.add(newCiv);
					sg.historicalCivs.add(newCiv);
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
			p.artefacts.remove(a);
			newP.strata.add(new LostArtefact("hidden", sg.year, a));
			rep.append("The ").append(a).append(" on ").append(p.name).append(" has been stolen and hidden on ").append(newP.name).append(".");
		}
	},
	PUTSCH() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			if (actor.govt == Government.DICTATORSHIP) {
				return;
			}
			SentientType rulers = sg.pick(actor.fullMembers);
			String oldName = actor.name;
			actor.fullMembers.clear();
			actor.fullMembers.add(rulers);
			actor.govt = Government.DICTATORSHIP;
			actor.updateName(sg.historicalCivs);
			rep.append("A military putsch turns the ").append(oldName).append(" into the ").append(actor.name).append(".");
		}
	},
	RELIGIOUS_REVIVAL() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			if (actor.govt == Government.DICTATORSHIP) {
				return;
			}
			String oldName = actor.name;
			actor.govt = Government.THEOCRACY;
			actor.updateName(sg.historicalCivs);
			rep.append("Religious fanatics sieze power in the ").append(oldName).append(" and declare the ").append(actor.name).append(".");
		}
	},
	MARKET_CRASH() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			actor.resources /= 5;
			rep.append("A market crash impoverishes the ").append(actor.name).append(".");
		}
	},
	DARK_AGE() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			rep.append("The ").append(actor.name).append(" enters a dark age.");
			actor.techLevel--;
			if (actor.techLevel == 0) {
				if (actor.fullMembers.size() > 1) {
					rep.append(" With the knowledge of faster-than-light travel lost, each planet in the empire has to fend for itself.");
				}
				for (Planet c : actor.colonies) {
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
				c.inhabitants.get(0).size = 1;
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
			p.pollution += 5;
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
				Civ newCiv = new Civ(sg.year, SentientType.ANTOIDS, bigPlanets.get(0), sg.pick(Government.values()), actor.resources / 2, sg.historicalCivs);
				newCiv.fullMembers.clear();
				newCiv.military = actor.military / 2;
				actor.military -= newCiv.military;
				actor.resources -= newCiv.resources;
				for (int i = 1; i < bigPlanets.size() / 2; i++) {
					newCiv.colonies.add(bigPlanets.get(i));
					bigPlanets.get(i).owner = newCiv;
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
				for (Planet c : actor.colonies) {
					if (bigPlanets.contains(c)) { continue; }
					if (sg.coin()) { newCiv.colonies.add(c); c.owner = newCiv; }
				}
				actor.colonies.removeAll(newCiv.colonies);
				newCiv.updateName(sg.historicalCivs);
				newCiv.relations.put(actor, Diplomacy.Outcome.WAR);
				actor.relations.put(newCiv, Diplomacy.Outcome.WAR);
				sg.civs.add(newCiv);
				sg.historicalCivs.add(newCiv);
				rep.append("The ").append(newCiv.name).append(" secedes from the ").append(actor.name).append(", leading to a civil war!");
			}
		}
	},
	PLAGUE() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			Planet p = sg.pick(actor.fullColonies());
			Plague plague = new Plague(sg);
			plague.affects.add(sg.pick(p.inhabitants).type);
			rep.append("The deadly ").append(plague.desc()).append(", arises on ").append(p.name).append(".");
			p.plagues.add(plague);
		}
	},
	STARVATION() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			Planet p = sg.pick(actor.fullColonies());
			int deaths = 0;
			for (Population pop : new ArrayList<Population>(p.inhabitants)) {
				int d = pop.size - pop.size / 2;
				deaths += 2;
				if (d >= pop.size) {
					p.dePop(pop, sg.year, null, "due to starvation", null);
				} else {
					pop.size -= d;
				}
			}
			rep.append("A famine breaks out on ").append(p.name).append(", killing ").append(deaths).append(" billion");
			if (p.population() == 0) {
				rep.append(", wiping out all sentient life.");
				p.deCiv(sg.year, null, "due to starvation");
			} else {
				rep.append(".");
			}
		}
	}
	;
	// SPAWN_ADVENTURER
	// SPAWN_PRIVATEER
	
	public void i(Civ actor, SpaceGen sg, StringBuilder rep) { return; }
	public void invoke(Civ actor, SpaceGen sg) {
		StringBuilder rep = new StringBuilder();
		i(actor, sg, rep);
		if (rep.length() > 0) { sg.l(rep.toString()); }
	}
}
