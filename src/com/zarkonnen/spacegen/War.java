package com.zarkonnen.spacegen;

import java.util.ArrayList;

public class War {
	public static void doWar(Civ actor, SpaceGen sg) {
		if (actor.military <= 0) { return; }
		ArrayList<Planet> targets = new ArrayList<Planet>();
		for (Planet p : sg.planets) {
			if (p.owner != null && p.owner != actor && actor.relation(p.owner) == Diplomacy.Outcome.WAR) {
				targets.add(p);
			}
		}
		if (targets.isEmpty()) { return; }
		Planet target = sg.pick(targets);
		
		int attack = actor.military * (2 + actor.techLevel);
		int defence = target.population() + (target.has(StructureType.MILITARY_BASE) ? 5 * actor.techLevel : 0);
		int attackRoll = sg.d(attack, 6);
		int defenceRoll = sg.d(defence, 6);
		if (attackRoll > defenceRoll) {
			actor.military -= sg.d(actor.military / 6 + 1);
			if (sg.d(6) < actor.govt.bombardP) {
				int deaths = 0;
				for (Population pop : new ArrayList<Population>(target.inhabitants)) {
					int pd = sg.d(pop.size) + 1;
					if (pd >= pop.size) {
						target.dePop(pop, sg.year, null, "due to orbital bombardment by the " + actor.name, null);
					} else {
						pop.size -= pd;
					}
					deaths += pd;
				}
				if (target.population() == 0) {
					target.deCiv(sg.year, null, "due to orbital bombardment by the " + actor.name);
					sg.l("The $cname attack and raze $pname.", actor, target);
				} else {
					for (Structure st : new ArrayList<Structure>(target.structures)) {
						if (sg.coin()) {
							target.strata.add(new Ruin(st, sg.year, null, "through orbital bombardment by the " + actor.name));
						}
					}
					sg.l("The $cname attack and subject $pname to orbital bombardment, killing " + deaths + " billion.", actor, target);
				}
			} else {
				for (Structure st : new ArrayList<Structure>(target.structures)) {
					if (sg.p(4)) {
						target.strata.add(new Ruin(st, sg.year, null, "during the invasion of the " + actor.name));
					}
				}
				if (target.population() > 0) {
					int deaths = 0;
					for (Population pop : new ArrayList<Population>(target.inhabitants)) {
						int pd = sg.d(pop.size - pop.size / 2);
						if (pd >= target.population()) { break; }
						if (pd >= pop.size) {
							target.dePop(pop, sg.year, null, "during the invasion of the " + actor.name, null);
						} else {
							pop.size -= pd;
						}
						deaths += pd;
					}
					sg.l("The $cname conquer $pname, killing " + deaths + " billion in the process.", actor, target);
				} else {
					sg.l("The $cname conquer $pname.", actor, target);
				}
				
				target.owner.colonies.remove(target);
				target.owner = actor;
				actor.colonies.add(target);
			}
		} else {
			for (Structure st : new ArrayList<Structure>(target.structures)) {
				if (sg.p(6)) {
					target.strata.add(new Ruin(st, sg.year, null, "during an attack by the " + actor.name));
				}
			}
			actor.military -= sg.d(actor.military / 3 + 1);
			sg.l("The " + target.owner.name + " repel the " + actor.name + " at " + target.name + ".");
		}
	}
}
