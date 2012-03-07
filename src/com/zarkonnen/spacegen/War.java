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
		
		if (actor.has(ArtefactType.Device.TIME_MACHINE)) {
			sg.l("The $name use their time machine to erase their hated enemies, the " + target.owner.name + ".", actor);
			Civ victim = target.owner;
			for (Planet p : new ArrayList<Planet>(victim.colonies)) {
				p.deCiv(sg.year / 2, null, "by a time vortex");
				p.owner = null;
			}
			sg.civs.remove(victim);
			Planet p = sg.pick(sg.planets);
			if (p.strata.isEmpty()) {
				p.strata.add(new LostArtefact("lost", sg.year / 4, actor.use(ArtefactType.Device.TIME_MACHINE)));
			} else {
				p.strata.add(0, new LostArtefact("lost", p.strata.get(0).time() / 2, actor.use(ArtefactType.Device.TIME_MACHINE)));
			}
			return;
		}
		if (actor.has(ArtefactType.Device.KILLER_MEME)) {
			sg.l("The $name use their memetic weapon against the " + target.owner.name + ".", actor);
			BadCivEvent.MASS_HYSTERIA.invoke(target.owner, sg);
			target.strata.add(new LostArtefact("forgotten", sg.year, actor.use(ArtefactType.Device.KILLER_MEME)));
			return;
		}
		if (actor.has(ArtefactType.Device.UNIVERSAL_COMPUTER_VIRUS)) {
			sg.l("The $name use their universal computer virus against the " + target.owner.name + ".", actor);
			BadCivEvent.MARKET_CRASH.invoke(target.owner, sg);
			target.strata.add(new LostArtefact("forgotten", sg.year, actor.use(ArtefactType.Device.UNIVERSAL_COMPUTER_VIRUS)));
			return;
		}
		if (actor.has(ArtefactType.Device.ARTIFICIAL_PLAGUE)) {
			sg.l("The $name use their artificial plague against the " + target.owner.name + ".", actor);
			BadCivEvent.PLAGUE.invoke(target.owner, sg);
			actor.use(ArtefactType.Device.ARTIFICIAL_PLAGUE);
			return;
		}
		
		Civ enemy = target.owner;
		
		int attack = actor.military * (2 + (actor.techLevel + 2 * actor.weapLevel));
		int defence = target.population() + (target.has(StructureType.Standard.MILITARY_BASE) ? 5 * (target.owner.techLevel + 2 * target.owner.weapLevel) : 0);
		if (target.has(SentientType.URSOIDS.specialStructure)) {
			defence += 4;
		}
		int attackRoll = sg.d(attack, 6);
		int defenceRoll = sg.d(defence, 6);
		if (attackRoll > defenceRoll) {
			actor.military -= sg.d(actor.military / 6 + 1);
			if (sg.d(6) < actor.govt.bombardP) {
				if (actor.has(ArtefactType.Device.PLANET_DESTROYER)) {
					target.deLive(sg.year, null, "when the planet was scoured by a superweapon of the " + actor.name);
					sg.l("The $cname attack $pname and use their planet destroyer to turn it into a lifeless cinder.", actor, target);
					return;
				}
				if (target.has(SentientType.DEEP_DWELLERS.specialStructure)) {
					for (Structure st : new ArrayList<Structure>(target.structures)) {
						if (sg.p(3)) {
							target.strata.add(new Ruin(st, sg.year, null, "through orbital bombardment by the " + actor.name));
							target.structures.remove(st);
						}
					}
					sg.l("The $cname attack $pname, a colony of the " + enemy.name + ", and subject it to orbital bombardment. Its inhabitats hide in the dome built deep in the planet's crust and escape harm.", actor, target);
					return;
				}
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
					sg.l("The $cname attack and raze $pname, a colony of the " + enemy.name + ".", actor, target);
				} else {
					for (Structure st : new ArrayList<Structure>(target.structures)) {
						if (sg.coin()) {
							target.strata.add(new Ruin(st, sg.year, null, "through orbital bombardment by the " + actor.name));
							target.structures.remove(st);
						}
					}
					sg.l("The $cname attack $pname, a colony of the " + enemy.name + ", and subject it to orbital bombardment, killing " + deaths + " billion.", actor, target);
				}
			} else {
				if (actor.has(ArtefactType.Device.MIND_CONTROL_DEVICE)) {
					sg.l("The $cname conquer $pname, a colony of the " + enemy.name + ", using their mind control device to gain control of the planet from orbit.", actor, target);
				} else {
					for (Structure st : new ArrayList<Structure>(target.structures)) {
						if (sg.p(4)) {
							target.strata.add(new Ruin(st, sg.year, null, "during the invasion of the " + actor.name));
							target.structures.remove(st);
						}
					}
					if (target.population() > 0) {
						int deaths = 0;
						for (Population pop : new ArrayList<Population>(target.inhabitants)) {
							int pd = sg.d(pop.size - pop.size / 2);
							if (pd >= target.population()) { pd = 1; }
							if (pd >= target.population()) { break; }
							if (pd >= pop.size) {
								target.dePop(pop, sg.year, null, "during the invasion of the " + actor.name, null);
							} else {
								pop.size -= pd;
							}
							deaths += pd;
						}
						if (deaths > 0) {
							sg.l("The $cname conquer $pname, a colony of the " + enemy.name + ", killing " + deaths + " billion in the process.", actor, target);
						} else {
							sg.l("The $cname conquer $pname, a colony of the " + enemy.name + ".", actor, target);
						}
					} else {
						sg.l("The $cname conquer $pname, a colony of the " + enemy.name + ".", actor, target);
					}
				}
				
				for (Artefact a : target.artefacts) {
					if (!(a.type instanceof ArtefactType.Device)) { continue; }
					sg.l("The $cname gain control of the " + a.type.getName() + " on $pname.", actor, target);
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
