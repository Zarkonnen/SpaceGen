package com.zarkonnen.spacegen;

import java.util.ArrayList;

public enum AgentType {
	PIRATE() {
		@Override
		public void behave(Agent a, SpaceGen sg) {
			// move
			a.p = sg.pick(sg.planets);
			int age = sg.year - a.birth;
			if (age > 8 + sg.d(6)) {
				sg.l("The pirate " + a.name + " dies and is buried on " + a.p.name + ".");
				Artefact art = new Artefact(sg.year, null, ArtefactType.PIRATE_TOMB, "Tomb of the Pirate " + a.name);
				art.specialValue = a.resources + a.fleet;
				a.p.strata.add(new LostArtefact("buried", sg.year, art));
				sg.agents.remove(a);
				return;
			}
			if (a.p.owner != null) {
				int tribute = sg.d(8) + 1;
				if (a.p.owner.resources >= tribute && !sg.p(4)) {
					a.p.owner.resources -= tribute;
					a.resources += tribute;
					sg.l("The pirate " + a.name + " receives tribute from " + a.p.name + " of the " + a.p.owner.name + ".");
				} else {
					int attack = a.fleet * 4;
					int defence = a.p.population() + (a.p.has(StructureType.Standard.MILITARY_BASE) ? 5 * (a.p.owner.techLevel + 2 * a.p.owner.weapLevel) : 0);
					if (a.p.has(SentientType.Base.URSOIDS.specialStructure)) {
						defence += 4;
					}
					int attackRoll = sg.d(attack, 6);
					int defenceRoll = sg.d(defence, 6);
					Planet target = a.p;
					if (attackRoll > defenceRoll) {
						if (target.has(SentientType.Base.DEEP_DWELLERS.specialStructure)) {
							for (Structure st : new ArrayList<Structure>(target.structures)) {
								if (sg.p(3)) {
									target.strata.add(new Ruin(st, sg.year, null, "through orbital bombardment by the pirate " + a.name));
									target.structures.remove(st);
								}
							}
							sg.l("The pirate " + a.name + " subjects " + target.name + " to orbital bombardment. Its inhabitants hide in the dome deep in the planet's crust and escape harm.");
							return;
						}
						int deaths = 0;
						for (Population pop : new ArrayList<Population>(target.inhabitants)) {
							int pd = sg.d(pop.size) + 1;
							if (pd >= pop.size) {
								target.dePop(pop, sg.year, null, "due to orbital bombardment by the pirate " + a.name, null);
							} else {
								pop.size -= pd;
							}
							deaths += pd;
						}
						if (target.population() == 0) {
							target.deCiv(sg.year, null, "due to orbital bombardment by the pirate " + a.name);
							sg.l("The pirate " + a.name + " subjects " + target.name + " to orbital bombardment.");
						} else {
							for (Structure st : new ArrayList<Structure>(target.structures)) {
								if (sg.coin()) {
									target.strata.add(new Ruin(st, sg.year, null, "through orbital bombardment by the pirate " + a.name));
									target.structures.remove(st);
								}
							}
							sg.l("The pirate " + a.name + " subjects " + target.name + " to orbital bombardment, killing " + deaths + " billion.");
						}
					} else {
						sg.l("The $name defeats the pirate " + a.name + ".", a.p.owner);
						a.p.owner.resources += a.resources / 2;
						a.p.owner.military = a.p.owner.military * 5 / 6;
						sg.agents.remove(a);
					}
				}
			} else {
				// Buy more ships or leave pirate treasure.
				if (a.resources > 5) {
					if (sg.p(3)) {
						sg.l("The pirate " + a.name + " buries a hoard of treasure on " + a.p.name + ".");
						Artefact art = new Artefact(sg.year, null, ArtefactType.PIRATE_HOARD, "Hoard of the Pirate " + a.name);
						art.specialValue = a.resources - 2;
						a.resources = 2;
						a.p.strata.add(new LostArtefact("buried", sg.year, art));
					} else {
						a.fleet++;
						a.resources -= 2;
					}
				}
			}
		}
	},
	ADVENTURER() {
		@Override
		public void behave(Agent a, SpaceGen sg) {
			int age = sg.year - a.birth;
			if (!sg.civs.contains(a.originator) || age > 8 + sg.d(6)) {
				sg.l("The space adventurer " + a.name + " dies and is buried on " + a.p.name + ".");
				Artefact art = new Artefact(sg.year, null, ArtefactType.ADVENTURER_TOMB, "Tomb of " + a.name);
				a.p.strata.add(new LostArtefact("buried", sg.year, art));
				art.specialValue = a.resources / 3 + a.fleet / 5 + 1;
				sg.agents.remove(a);
				return;
			}
			a.p = sg.pick(sg.planets);
			if (a.p.owner == a.originator) {
				while (a.resources > 4) {
					a.fleet++;
					a.resources -= 4;
				}
				// Missions!
				// KILL PIRATE
				Agent pir = null;
				lp: for (Planet p : sg.planets) {
					for (Agent ag : sg.agents) {
						if (ag.type != AgentType.PIRATE) { continue; }
						if (ag.p == p) {
							pir = ag;
							break lp;
						}
					}
				}
				if (pir != null) {
					sg.l(a.name + " is sent on a mission to defeat the pirate " + pir.name + " by the government of " + a.p.name + ".");
					if (sg.coin()) {
						sg.l(a.name + " fails to find any trace of the pirate " + pir.name + ".");
						return;
					}
					sg.l(a.name + " tracks down the pirate " + pir.name + " in orbit around " + pir.p.name + ".");
					// FAIGHTH!
					int attack = a.fleet * 4;
					int defence = pir.fleet * 3;
					int attackRoll = sg.d(attack, 6);
					int defenceRoll = sg.d(defence, 6);
					if (attackRoll > defenceRoll) {
						sg.l(a.name + " defeats the pirate " + pir.name + " - the skies of " + a.p.name + " are safe again.");
						sg.agents.remove(pir);
						a.resources += pir.resources / 2;
						a.originator.resources += pir.resources / 2;
					} else {
						if (a.fleet < 2) {
							sg.l(a.name + " is defeated utterly by the pirate.");
							sg.agents.remove(a);
						} else {
							sg.l(a.name + " is defeated by the pirate " + pir.name + " and flees back to " + a.p.name + ".");
							a.fleet /= 2;
						}
					}
					return;
				}
				
				// KILL SM
				
				// PEACE MISSION
				Civ enemy = null;
				for (Civ c : sg.civs) {
					if (c != a.originator && a.originator.relation(c) == Diplomacy.Outcome.WAR) {
						enemy = c;
					}
				}
				if (enemy != null && sg.p(4)) {
					sg.l("The " + a.originator.name + " send " + a.name + " on a mission of peace to the " + enemy.name + ".");
					if (sg.coin()) {
						sg.l("The expert diplomacy of " + a.name + " is successful: the two empires are at peace.");
						a.originator.relations.put(enemy, Diplomacy.Outcome.PEACE);
						enemy.relations.put(a.originator, Diplomacy.Outcome.PEACE);
					} else {
						sg.l("Unfortunately, the peace mission fails. " + a.name + " hastily retreats to " + a.p.name + ".");
					}
					return;
				}
				
				// Steal U
				if (enemy != null) {
					for (Planet p : enemy.colonies) {
						if (!p.artefacts.isEmpty()) {
							Artefact art = p.artefacts.get(0);
							sg.l("The " + a.originator.name + " send " + a.name + " on a mission to steal the " + art.type.getName() + " on " + p.name + ".");
							if (sg.coin()) {
								Planet lc = a.originator.largestColony();
								sg.l(a.name + " successfully acquires the " + art.type.getName() + " and delivers it to " + lc.name + ".");
								p.artefacts.remove(art);
								lc.artefacts.add(art);
							} else {
								if (sg.p(3)) {
									sg.l("The " + enemy.name + " capture and execute " + a.name + " for trying to steal the " + art.type.getName() + ".");
									sg.agents.remove(a);
								} else {
									sg.l("The attempt to steal the " + art.type.getName() + " fails, and " + a.name + " swiftly retreats to " + a.p.name + " to avoid capture.");
								}
							}
							return;
						}
					}
				}
			}
		}
	},
	SHAPE_SHIFTER() {
		@Override
		public void behave(Agent a, SpaceGen sg) {
			if (a.p.inhabitants.isEmpty()) {
				sg.agents.remove(a);
				return;
			}
			if (a.p.population() > 1) {
				if (sg.p(6)) {
					Population victim = sg.pick(a.p.inhabitants);
					if (victim.size == 1) {
						sg.l("Shape-shifters devour the last remaining " + victim.type.getName() + " on " + a.p.name + ".");
						a.p.dePop(victim, sg.year, null, "through predation by shape-shifters", null);
					} else {
						victim.size--;
					}
				}
				if (sg.p(40)) {
					sg.l("The inhabitants of " + a.p.name + " manage to identify the shape-shifters among them and exterminate them.");
					sg.agents.remove(a);
				}
			} else {
				sg.l("The population of " + a.p.name + " turn out to be all shape-shifters. The colony collapses as the shape-shifters need real sentients to keep up their mimicry.");
				a.p.deCiv(sg.year, null, "when the entire population of the planet turned out to be shape-shifters");
				if (!a.p.lifeforms.contains(SpecialLifeform.SHAPE_SHIFTER)) {
					a.p.lifeforms.add(SpecialLifeform.SHAPE_SHIFTER);
				}
				sg.agents.remove(a);
			}
		}
	},
	ULTRAVORES() {
		@Override
		public void behave(Agent a, SpaceGen sg) {
			if (a.p.inhabitants.isEmpty() || a.p.owner == null) {
				sg.agents.remove(a);
				return;
			}
			if (sg.p(6)) {
				if (a.p.population() > 1) {
					Population victim = sg.pick(a.p.inhabitants);
					if (victim.size == 1) {
						sg.l("A billion " + victim.type.getName() + " on " + a.p.name + " are devoured by ultravores.");
						a.p.dePop(victim, sg.year, null, "through predation by ultravores", null);
					} else {
						victim.size--;
					}
				} else {
					sg.l("Ultravores devour the final inhabitants of " + a.p.name + ".");
					a.p.deCiv(sg.year, null, "through predation by ultravores");
				}
				if (sg.p(3) && a.p.owner != null) {
					lp: for (Planet p : a.p.owner.fullColonies()) {
						for (Agent ag : sg.agents) {
							if (ag.type == AgentType.ULTRAVORES && ag.p == p) { continue lp; }
						}
						Agent ag = new Agent(ULTRAVORES, sg.year, "Hunting pack of Ultravores");
						ag.p = p;
						sg.agents.add(ag);
						break;
					}
				}
			}
		}
	};/*,
	ADVENTURER,
	MIMIC,
	ULTRAVORE,
	SPACE_MONSTER*/;
	
	public abstract void behave(Agent a, SpaceGen sg);
}
