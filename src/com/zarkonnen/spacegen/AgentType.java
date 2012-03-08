package com.zarkonnen.spacegen;

import java.util.ArrayList;

public enum AgentType {
	PIRATE() {
		@Override
		public String describe(Agent a, SpaceGen sg) {
			String d = "In orbit: The pirate " + a.name + ", a " + a.st.name;
			if (a.fleet < 2) {
				d += ".";
			} else {
				d += ", commanding a fleet of " + a.fleet + " ships.";
			}
			return d;
		}
		
		@Override
		public void behave(Agent a, SpaceGen sg) {
			// move
			a.p = sg.pick(sg.planets);
			int age = sg.year - a.birth;
			if (age > 8 + sg.d(6)) {
				sg.l("The pirate " + a.name + " dies and is buried on " + a.p.name + ".");
				Artefact art = new Artefact(sg.year, (Civ) null, ArtefactType.PIRATE_TOMB, "Tomb of the Pirate " + a.name);
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
						Artefact art = new Artefact(sg.year, (Civ) null, ArtefactType.PIRATE_HOARD, "Hoard of the Pirate " + a.name);
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
		public String describe(Agent a, SpaceGen sg) {
			String d = "In orbit: The adventurer " + a.name + ", a member of the " + a.st.name + ", serving the " + a.originator.name;
			if (a.fleet < 2) {
				d += ".";
			} else {
				d += ", commanding a fleet of " + a.fleet + " ships.";
			}
			return d;
		}
		
		boolean encounter(Agent a, SpaceGen sg, Agent ag) {
			switch (ag.type) {
				case ROGUE_AI:
					if (sg.coin()) {
						if (a.fleet <= 3) {
							sg.l(a.name + " is killed by the rogue AI " + ag.name + ".");
							sg.agents.remove(a);
							a.p.strata.add(new LostArtefact("crashed", sg.year, new Artefact(sg.year, a.originator, ArtefactType.WRECK,
									"wreck of the flagship of " + a.name + ", destroyed by the rogue AI " + ag.name)));
						} else {
							int loss = sg.d(a.fleet - 1) + 1;
							sg.l(a.name + " is attacked by the rogue AI " + ag.name + " and has to retreat, losing " + loss + " ships.");
							a.fleet -= loss;
							a.p.strata.add(new LostArtefact("crashed", sg.year, new Artefact(sg.year, a.originator, ArtefactType.WRECK,
									"shattered wrecks of " + loss + " spaceships of the fleet of " + a.name + ", destroyed by the rogue AI " + ag.name)));
						}
					} else {
						sg.l(a.name + " manages to confuse the rogue AI " + ag.name + " with a clever logic puzzle, distracting it long enough to shut it down.");
						a.resources += 5;
						sg.agents.remove(ag);
					}
					return true;
				case SPACE_PROBE:
					if (sg.coin()) {
						sg.l(a.name + " attempts to reason with the space probe " + ag.name + " but triggers its self-destruct mechanism.");
						if (sg.coin()) {
							a.p.deLive(sg.year, null, "due to the self-destruction of the insane space probe " + ag.name);
							sg.l("The resulting shockwave exterminates all life on " + a.p.name + ".");
							sg.agents.remove(ag);
							sg.agents.remove(a);
						}
					} else {
						sg.l(a.name + " successfully reasons with the insane space probe " + ag.name + ", which transfers its accumulated information into the fleet's data banks and then shuts down.");
						a.originator.techLevel += 3;
						sg.agents.remove(ag);
					}
					return true;
				case SPACE_MONSTER:
					int attackRoll = sg.d(a.fleet, 6);
					int defenseRoll = sg.d(4, 6);
					if (attackRoll > defenseRoll) {
						sg.l(a.name + " defeats the " + ag.name + " in orbit around " + a.p.name + ".");
						sg.agents.remove(ag);
						if (a.p.owner != null) {
							sg.l("The " + a.p.owner + " rewards the adventurer handsomely.");
							a.resources += a.p.owner.resources / 3;
							a.p.owner.resources = a.p.owner.resources * 2 / 3;
						}
					} else {
						int loss = sg.d(2) + 2;
						if (a.fleet - loss <= 0) {
							sg.l("The " + ag.name + " in orbit around " + a.p.name + " attacks and kills " + a.name + ".");
							sg.agents.remove(a);
							a.p.strata.add(new LostArtefact("crashed", sg.year, new Artefact(sg.year, a.originator, ArtefactType.WRECK,
									"wreck of the flagship of " + a.name + ", destroyed by a " + ag.name)));
						} else {
							a.fleet -= loss;
							sg.l("The " + ag.name + " attacks the fleet of " + a.name + " near " + a.p.name + " destroying " + loss + " ships.");
							a.p.strata.add(new LostArtefact("crashed", sg.year, new Artefact(sg.year, a.originator, ArtefactType.WRECK,
									"shattered wrecks of " + loss + " spaceships of the fleet of " + a.name + ", destroyed by a " + ag.name)));
						}
					}
					return true;
			}
			return false;
		}
		
		@Override
		public void behave(Agent a, SpaceGen sg) {
			a.p = sg.pick(sg.planets);
			int age = sg.year - a.birth;
			if (!sg.civs.contains(a.originator) || age > 8 + sg.d(6)) {
				sg.l("The space adventurer " + a.name + " dies and is buried on " + a.p.name + ".");
				Artefact art = new Artefact(sg.year, (Civ) null, ArtefactType.ADVENTURER_TOMB, "Tomb of " + a.name);
				a.p.strata.add(new LostArtefact("buried", sg.year, art));
				art.specialValue = a.resources / 3 + a.fleet / 5 + 1;
				sg.agents.remove(a);
				return;
			}
			
			for (Agent ag : sg.agents) {
				if (ag != a && ag.p == a.p) {
					if (encounter(a, sg, ag)) { return; }
				}
			}
			
			if (sg.p(3) && a.p.owner != null && a.p.owner != a.originator && a.originator.relation(a.p.owner) == Diplomacy.Outcome.WAR) {
				// Show some initiative!
				String act = sg.pick(new String[] {
					" raids the treasury on ",
					" intercepts a convoy near ",
					" steals jewels from ",
					" steals a spaceship from the navy of ",
					" extorts money from "
				});
				sg.l(a.name + act + a.p.name + ", a planet of the enemy " + a.p.owner.name + ".");
				a.resources += 2;
				a.p.owner.resources = a.p.owner.resources * 5 / 6;
				return;
			}
			
			if (a.p.owner == null || (a.p.owner != a.originator && a.originator.relation(a.p.owner) == Diplomacy.Outcome.PEACE)) {
				// Exploration
				StringBuilder rep = new StringBuilder();
				boolean major = false;
				rep.append("An expedition led by ").append(a.name).append(" explores ").append(a.p.name).append(". ");
				
				// - find artefacts
				// - exterminate bad wildlife
				
				boolean runAway = false;
				lp: for (SpecialLifeform slf : new ArrayList<SpecialLifeform>(a.p.lifeforms)) {
					if (sg.coin()) { continue; }
					switch (slf) {
						case BRAIN_PARASITE:
						case ULTRAVORE:
						case SHAPE_SHIFTER:
							String monster = slf.name.toLowerCase();;
							major = true;
							rep.append("They encounter the local ").append(monster);
							if (sg.p(3)) {
								rep.append(" and exterminate them. ");
								a.p.lifeforms.remove(slf);
							} else {
								if (a.fleet < 1) {
									rep.append(". In a desperate attempt to stop them, ").append(a.name).append(" activates the ship's self-destruct sequence.");
									runAway = true;
									sg.agents.remove(a);
									break lp;
								} else {
									rep.append(". In a desperate attempt to stop them, ").append(a.name).append(" has half of the exploration fleet blasted to bits.");
									runAway = true;
									a.fleet /= 2;
									break lp;
								}
							}
							break;
					}
				}
				
				if (runAway) {
					sg.l(rep.toString());
					return;
				}
				
				// Inhabs
				if (!a.p.inhabitants.isEmpty()) {
					major = true;
					rep.append("They trade with the local ").append(sg.pick(a.p.inhabitants).type.name).append(". ");
					a.p.evoPoints += 5000;
					a.resources += 2;
				}
				
				// Archeology!
				Planet p = a.p;
				for (int stratNum = 0; stratNum < p.strata.size(); stratNum++) {
					Stratum stratum = p.strata.get(p.strata.size() - stratNum - 1);
					if (sg.p(4 + stratNum * 2)) {
						if (stratum instanceof Fossil) {
							rep.append("They discover: ").append(stratum.toString()).append(" ");
							a.originator.science++;
						}
						if (stratum instanceof Remnant) {
							rep.append("They discover: ").append(stratum.toString()).append(" ");
							a.resources++;
							Remnant r = (Remnant) stratum;
							Planet homeP = a.originator.largestColony();
							if (r.plague != null && sg.d(6) < r.plague.transmissivity) {
								boolean affects = false;
								for (Population pop : homeP.inhabitants) {
									if (r.plague.affects.contains(pop.type)) {
										affects = true;
									}
								}
								
								if (affects) {
									homeP.plagues.add(new Plague(r.plague));
									rep.append(" Unfortunately, members of the expedition catch the ").append(r.plague.name).append(" from their exploration of the ancient tombs, infecting ").append(homeP.name).append(" upon their return. ");
									major = true;
								}
							}
						}
						if (stratum instanceof LostArtefact) {
							LostArtefact la = (LostArtefact) stratum;
							if (la.artefact.type == ArtefactType.PIRATE_TOMB || la.artefact.type == ArtefactType.PIRATE_HOARD || la.artefact.type == ArtefactType.ADVENTURER_TOMB) {
								rep.append("The expedition loots the ").append(la.artefact.desc).append(". ");
								a.resources += la.artefact.specialValue;
								p.strata.remove(stratum);
								stratNum--;
								continue;
							}
							if (la.artefact.type == ArtefactType.Device.STASIS_CAPSULE) {
								if (!sg.civs.contains(la.artefact.creator)) {
									rep.append("They open a stasis capsule from the ").append(la.artefact.creator.name).append(", which arises once more!");
									sg.civs.add(la.artefact.creator);
									la.artefact.creator.techLevel = la.artefact.creatorTechLevel;
									la.artefact.creator.resources = 10;
									la.artefact.creator.military = 10;
									if (p.owner != null) {
										p.owner.relations.put(la.artefact.creator, Diplomacy.Outcome.WAR);
										la.artefact.creator.relations.put(p.owner, Diplomacy.Outcome.WAR);
										p.owner.colonies.remove(p);
									}
									la.artefact.creator.colonies.clear();
									la.artefact.creator.colonies.add(p);
									p.owner = la.artefact.creator;
									boolean inserted = false;
									for (Population pop : p.inhabitants) {
										if (pop.type == la.artefact.st) {
											pop.size += 3;
											inserted = true;
											break;
										}
									}
									if (!inserted) {
										p.inhabitants.add(new Population(la.artefact.st, 3));
									}
									la.artefact.creator.birthYear = sg.year;
									p.strata.remove(stratum);
									stratNum--;
									break;
								}
								continue;
							}
							if (la.artefact.type == ArtefactType.Device.MIND_ARCHIVE) {
								rep.append("They encounter a mind archive of the ").append(la.artefact.creator.name).append(" which brings new knowledge and wisdom to the ").append(a.originator.name).append(". ");
								major = true;
								a.originator.techLevel = Math.max(a.originator.techLevel, la.artefact.creatorTechLevel);
								continue;
							}
							if (la.artefact.type == ArtefactType.WRECK) {
								rep.append("They recover: ").append(stratum).append(" ");
								p.strata.remove(stratum);
								a.resources += 3;
								stratNum--;
								continue;
							}
							
							rep.append("They recover: ").append(stratum).append(" ");
							major = true;
							p.strata.remove(stratum);
							a.resources++;
							sg.pick(a.originator.colonies).artefacts.add(la.artefact);
							stratNum--;
						}
					}
				}
				
				if (major) {
					sg.l(rep.toString());
					return;
				}
				
				return;
			}
			
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
				Agent mon = null;
				lp: for (Planet p : sg.planets) {
					for (Agent ag : sg.agents) {
						if (ag.type != AgentType.SPACE_MONSTER) { continue; }
						if (ag.p == p) {
							mon = ag;
							break lp;
						}
					}
				}
				if (mon != null) {
					sg.l(a.name + " is sent on a mission to defeat the " + mon.name + " at " + mon.p.name + ".");
					a.p = mon.p;
					encounter(a, sg, mon);
					return;
				}
				
				Agent ai = null;
				lp: for (Planet p : sg.planets) {
					for (Agent ag : sg.agents) {
						if (ag.type != AgentType.ROGUE_AI) { continue; }
						if (ag.p == p) {
							ai = ag;
							break lp;
						}
					}
				}
				if (ai != null) {
					sg.l(a.name + " is sent on a mission to stop the rogue AI " + ai.name + " at " + ai.p.name + ".");
					a.p = ai.p;
					encounter(a, sg, ai);
					return;
				}
				
				Agent pr = null;
				lp: for (Planet p : sg.planets) {
					for (Agent ag : sg.agents) {
						if (ag.type != AgentType.SPACE_PROBE) { continue; }
						if (ag.p == p) {
							pr = ag;
							break lp;
						}
					}
				}
				if (ai != null) {
					sg.l(a.name + " is sent on a mission to stop the insane space probe " + pr.name + " threatening " + pr.p.name + ".");
					a.p = pr.p;
					encounter(a, sg, pr);
					return;
				}
				
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
		public String describe(Agent a, SpaceGen sg) {
			return "A pack of shape-shifters hiding amongst the local population.";
		}
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
		public String describe(Agent a, SpaceGen sg) {
			return "A pack of ultravores, incredibly dangerous predators.";
		}
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
	},
	SPACE_MONSTER() {
		@Override
		public String describe(Agent a, SpaceGen sg) {
			return "In orbit: A " + a.name + " threatening the planet.";
		}
		@Override
		public void behave(Agent a, SpaceGen sg) {
			if (sg.p(500)) {
				sg.l("The " + a.name + " devours all life on " + a.p.name + ".");
				a.p.deLive(sg.year, null, "due to the attack of a " + a.name);
				return;
			}
			if (sg.p(8) && a.p.population() > 2) {
				Population t = sg.pick(a.p.inhabitants);
				if (t.size == 1) {
					sg.l("The " + a.name + " devours the last of the local " + t.type.name + " on " + a.p.name + ".");
					a.p.dePop(t, sg.year, null, "due to predation by a " + a.name, null);
				} else {
					sg.l("The " + a.name + " devours one billion " + t.type.name + " on " + a.p.name + ".");
					t.size--;
				}
				return;
			}
			if (sg.p(20)) {
				sg.l("The " + a.name + " leaves the orbit of " + a.p.name + " and heads back into deep space.");
				sg.agents.remove(a);
				return;
			}
		}
	},
	SPACE_PROBE() {
		@Override
		public String describe(Agent a, SpaceGen sg) {
			return "In orbit: The insane space probe " + a.name + " threatening the planet.";
		}
		@Override
		public void behave(Agent a, SpaceGen sg) {
			if (a.p == null) {
				a.timer--;
				if (a.timer == 0) {
					a.p = a.target;
					sg.l("The space probe " + a.name + " returns to " + a.p.name + ".");
					if (a.p.owner == a.originator) {
						sg.l("The " + a.originator.name + " gains a wealth of new knowledge as a result.");
						a.originator.techLevel += 3;
						sg.agents.remove(a);
						return;
					} else {
						sg.l("Unable to contact the " + a.originator.name + " that launched it, the probe goes insane.");
					}
				}
				return;
			}
			if (sg.p(8) && a.p.population() > 2) {
				Population t = sg.pick(a.p.inhabitants);
				if (t.size == 1) {
					sg.l("The insane space probe " + a.name + " bombards " + a.p.name + ", wiping out the local " + t.type.name + ".");
					a.p.dePop(t, sg.year, null, "due to bombardment by the insane space probe " + a.name, null);
				} else {
					sg.l("The insane space probe " + a.name + " bombards " + a.p.name + ", killing one billion " + t.type.name + ".");
					t.size--;
				}
				return;
			}
			if (sg.p(40)) {
				sg.l("The insane space probe " + a.name + " crashes into " + a.p.name + ", wiping out all life on the planet.");
				a.p.deLive(sg.year, null, "due to the impact of the space probe " + a.name);
				sg.agents.remove(a);
				return;
			}
		}
	},
	ROGUE_AI() {
		@Override
		public String describe(Agent a, SpaceGen sg) {
			return "In orbit: The rogue AI " + a.name + ".";
		}
		@Override
		public void behave(Agent a, SpaceGen sg) {
			if (a.timer > 0) {
				a.timer--;
				if (a.timer == 0) {
					a.p = sg.pick(sg.planets);
					sg.l("The rogue AI " + a.name + " reappears on " + a.p.name + ".");
				}
				return;
			}
			if (sg.p(10)) {
				a.p = sg.pick(sg.planets);
			}
			if (sg.p(50)) {
				sg.l("The rogue AI " + a.name + " vanishes without a trace.");
				a.timer = 40 + sg.d(500);
				a.p = null;
				return;
			}
			if (sg.p(80)) {
				sg.l("The rogue AI " + a.name + " vanishes without a trace.");
				sg.agents.remove(a);
				return;
			}
			
			if (sg.p(40)) {
				for (Agent ag : sg.agents) {
					if (ag == a) { continue; }
					if (ag.p != a.p) { continue; }
					Artefact art = null;
					switch (ag.type) {
						case ADVENTURER:
							sg.l("The rogue AI " + a.name + " encases the adventurer " + ag.name + " in a block of time ice.");
							art = new Artefact(sg.year, "the rogue AI " + a.name, ArtefactType.TIME_ICE,
									"block of time ice encasing " + ag.name);
							break;
						case PIRATE:
							sg.l("The rogue AI " + a.name + " encases the pirate " + ag.name + " in a block of time ice.");
							art = new Artefact(sg.year, "the rogue AI " + a.name, ArtefactType.TIME_ICE,
									"block of time ice encasing the pirate " + ag.name);
							break;
						case SHAPE_SHIFTER:
							sg.l("The rogue AI " + a.name + " encases the shape-shifters on" + a.p.name + " in a block of time ice.");
							art = new Artefact(sg.year, "the rogue AI " + a.name, ArtefactType.TIME_ICE,
									"block of time ice, encasing a group of shape-shifters");
							break;
						case ULTRAVORES:
							sg.l("The rogue AI " + a.name + " encases a pack of ultravores on " + a.p.name + " in a block of time ice.");
							art = new Artefact(sg.year, "the rogue AI " + a.name, ArtefactType.TIME_ICE,
									"block of time ice, encasing a pack of ultravores");
							break;
						case ROGUE_AI:
							String newName = "Cluster " + sg.r.nextInt(100);
							sg.l("The rogue AI " + a.name + " merges with the rogue AI " + a.p.name + " into a new entity called " + newName + ".");
							a.name = newName;
							sg.agents.remove(ag);
							return;
					}
					if (art != null) {
						a.p.artefacts.add(art);
						sg.agents.remove(ag);
						return;
					}
				}
			}
			
			// Random mischief!
			if (a.p.owner != null) {
				if (sg.p(60)) {
					SentientType st = sg.pick(a.p.owner.fullMembers);
					String name = sg.pick(st.base.nameStarts) + sg.pick(st.base.nameEnds);
					String title = null;
					switch (a.p.owner.govt) {
						case DICTATORSHIP: title = "Emperor"; break;
						case FEUDAL_STATE: title = "King"; break;
						case REPUBLIC: title = "President"; break;
						case THEOCRACY: title = "Autarch"; break;
					}
					sg.l("The rogue AI " + a.name + " encases " + name + ", " + title + " of the " + a.p.owner.name + ", in a block of time ice.");
					a.p.artefacts.add(new Artefact(sg.year, "the rogue AI " + a.name, ArtefactType.TIME_ICE,
									"block of time ice, encasing " + name + ", " + title + " of the " + a.p.owner.name));
					return;
				}
				if (sg.p(60)) {
					sg.l("The rogue AI " + a.name + " crashes the " + a.p.name + " stock exchange.");
					a.p.owner.resources /= 2;
					return;
				}
				if (sg.p(30)) {
					ArtefactType.Device dt = sg.pick(ArtefactType.Device.values());
					if (dt == ArtefactType.Device.STASIS_CAPSULE) { return; }
					if (dt == ArtefactType.Device.MIND_ARCHIVE) { return; }
					Artefact dev = new Artefact(sg.year, "the rogue AI " + a.name, dt, dt.create(null, sg));
					sg.l("The rogue AI " + a.name + " presents the inhabitants of " + a.p.name + " with a gift: a " + dev.type.getName() + ".");
					a.p.artefacts.add(dev);
					return;
				}
				if (sg.p(20) && !a.p.artefacts.isEmpty()) {
					Artefact art = sg.pick(a.p.artefacts);
					Planet t = sg.pick(sg.planets);
					sg.l("The rogue AI " + a.name + " steals the " + art.desc + " on " + a.p.name + " and hides it on " + t.name + ".");
					a.p.artefacts.remove(art);
					t.strata.add(new LostArtefact("hidden", sg.year, art));
					return;
				}
			}
			if (!a.p.inhabitants.isEmpty()) {
				if (sg.p(40)) {
					Plague pl = new Plague(sg);
					pl.affects.add(a.p.inhabitants.get(0).type);
					for (int i = 1; i < a.p.inhabitants.size(); i++) {
						if (sg.coin()) {
							pl.affects.add(a.p.inhabitants.get(i).type);
						}
					}
					sg.l("The rogue AI " + a.name + " infects the inhabitants of " + a.p.name + " with " + pl.desc() + ".");
					a.p.plagues.add(pl);
					return;
				}
				if (a.p.population() > 2 && sg.p(25)) {
					for (Planet t : sg.planets) {
						if (t.habitable && t.owner == null) {
							Population victim = sg.pick(a.p.inhabitants);
							if (victim.size == 1) {
								t.inhabitants.add(victim);
							} else {
								t.inhabitants.add(new Population(victim.type, 1));
								victim.size--;
							}
							sg.l("The rogue AI " + a.name + " abducts a billion " + victim.type.name + " from " + a.p.name + " and dumps them on " + t.name + ".");
							return;
						}
					}
				}
			}
			
			if (a.p.habitable && sg.p(200) && a.p.owner == null) {
				SentientType st = SentientType.invent(sg, null, a.p, "They were created by the rogue AI " + a.name + " in " + sg.year + ".");
				sg.l("The rogue AI " + a.name + " uplifts the local " + st.name + " on " + a.p.name + ".");
				a.p.inhabitants.add(new Population(st, 3 + sg.d(3)));
				return;
			}
			
			if (a.p.habitable && sg.p(250) && a.p.owner == null) {
				SentientType st = SentientType.genRobots(sg, null, a.p, "They were created by the rogue AI " + a.name + " in " + sg.year + ".");
				sg.l("The rogue AI " + a.name + " creates " + st.name + " on " + a.p.name + ".");
				a.p.inhabitants.add(new Population(st, 3 + sg.d(3)));
				return;
			}
			
			if (!a.p.habitable && sg.p(500)) {
				sg.l("The rogue AI " + a.name + " terraforms " + a.p.name + ".");
				return;
			}
			
			if (sg.p(300) && a.p.habitable) {
				sg.l("The rogue AI " + a.name + " releases nanospores on " + a.p.name + ", destroying all life on the planet.");
				a.p.deLive(sg.year, null, "due to nanospores relased by " + a.name);
				return;
			}
			
			if (sg.civs.size() > 1 && sg.p(250)) {
				Civ c = sg.pick(sg.civs);
				ArrayList<Civ> others = new ArrayList<Civ>(sg.civs);
				others.remove(c);
				Civ c2 = sg.pick(others);
				if (c.relation(c2) == Diplomacy.Outcome.PEACE) {
					sg.l("The rogue AI " + a.name + " incites war between the " + c.name + " and the " + c2.name + ".");
					c.relations.put(c2, Diplomacy.Outcome.WAR);
					c2.relations.put(c, Diplomacy.Outcome.WAR);
				} else {
					sg.l("The rogue AI " + a.name + " brokers peace between the " + c.name + " and the " + c2.name + ".");
					c.relations.put(c2, Diplomacy.Outcome.PEACE);
					c2.relations.put(c, Diplomacy.Outcome.PEACE);
				}
			}
		}
	}/*,
	ADVENTURER,
	MIMIC,
	ULTRAVORE,
	SPACE_MONSTER*/;
	
	public static final String[] MONSTER_TYPES = { "worm", "cube", "crystal", "jellyfish" };
	
	public abstract void behave(Agent a, SpaceGen sg);
	
	public abstract String describe(Agent a, SpaceGen sg);
}
