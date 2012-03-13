package com.zarkonnen.spacegen;

import java.util.ArrayList;
import java.util.HashMap;

import static com.zarkonnen.spacegen.Stage.*;
import static com.zarkonnen.spacegen.Main.*;

public enum CivAction {
	EXPLORE_PLANET() {
		@Override
		public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			Sprite expedition = null;
			// Pick a planet to explore.
			Planet p = sg.pick(actor.reachables(sg));
			Planet srcP = actor.closestColony(p);
			if (p.getOwner() == actor) {
				animate(track(p.sprite));
			} else {
				animate(track(srcP.sprite));
				expedition = new Sprite(Imager.EXPEDITION, srcP.sprite.x - 48, srcP.sprite.y + 160 / 2 - 32 / 2);
				expedition.children.add(new CivSprite(actor, true));
				animate(add(expedition));
				animate(tracking(expedition, move(expedition, p.sprite.x - 48, p.sprite.y + 160 / 2 - 32 / 2)));
				animate(track(p.sprite));
			}
			expedite(actor, sg, rep, p, srcP);
			if (expedition != null) { animate(remove(expedition)); }
		}
		public void expedite(Civ actor, SpaceGen sg, StringBuilder rep, Planet p, Planet srcP) {
			if (p.getOwner() != null && p.getOwner() != actor) {
				Civ other = p.getOwner();
				// They meet a civ.
				rep.append("The ").append(actor.name).append(" send a delegation to ").append(p.name).append(". ");
				Diplomacy.Outcome outcome = Diplomacy.meet(actor, other, sg);
				rep.append(outcome.desc(other, actor.relation(other))).append(" ");
				if (outcome == Diplomacy.Outcome.UNION && (actor.has(SentientType.Base.PARASITES) || p.getOwner().has(SentientType.Base.PARASITES))) {
					outcome = Diplomacy.Outcome.PEACE;
				}
				if (outcome == Diplomacy.Outcome.UNION) {
					sg.civs.remove(other);
					Government newGovt = sg.pick(new Government[] { actor.getGovt(), other.getGovt()});
					actor.colonies.addAll(other.colonies);
					for (Planet c : new ArrayList<Planet>(other.colonies)) {
						c.setOwner(actor);
					}
					actor.setResources(actor.getResources() + other.getResources());
					actor.setScience(actor.getScience() + other.getScience());
					for (SentientType st : other.fullMembers) {
						if (!actor.fullMembers.contains(st)) {
							actor.fullMembers.add(st);
						}
					}
					HashMap<Civ, Diplomacy.Outcome> newRels = new HashMap<Civ, Diplomacy.Outcome>();
					for (Civ c : sg.civs) {
						if (c == actor || c == other) { continue; }
						if (actor.relation(c) == Diplomacy.Outcome.WAR ||
							other.relation(c) == Diplomacy.Outcome.WAR)
						{
							newRels.put(c, Diplomacy.Outcome.WAR);
							c.relations.put(actor, Diplomacy.Outcome.WAR);
						} else {
							newRels.put(c, Diplomacy.Outcome.PEACE);
							c.relations.put(actor, Diplomacy.Outcome.PEACE);
						}
					}
					actor.relations = newRels;
					actor.setGovt(newGovt, sg.historicalCivNames);
					rep.append("The two civilizations combine into the ").append(actor.name).append(". ");
					return;
				} else {
					if (actor.relation(other) != outcome) {
						actor.relations.put(other, outcome);
						other.relations.put(actor, outcome);
					} else {
						rep.delete(0, rep.length());
					}
				}
			} else {
				boolean major = false;
				rep.append("The ").append(actor.name).append(" explore ").append(p.name).append(". ");
				String base = rep.toString();
				// The wildlife.
				for (SpecialLifeform slf : p.lifeforms) {
					Planet victimP = null;
					switch (slf) {
						case BRAIN_PARASITE:
							if (!sg.p(3)) { break; }
							victimP = sg.pick(actor.colonies);
							int stolenResources = actor.getResources() / actor.colonies.size();
							Civ newCiv = new Civ(sg.year, SentientType.PARASITES, victimP, sg.pick(Government.values()), stolenResources, sg.historicalCivNames);
							rep.append("The expedition encounters brain parasites. Upon their return to ").append(victimP.name).append(", the parasites take over the brains of the planet's inhabitants, creating the ").append(newCiv.name).append(".");
							victimP.setOwner(newCiv);
							actor.colonies.remove(victimP);
							sg.civs.add(newCiv);
							sg.historicalCivNames.add(newCiv.name);
							newCiv.relations.put(actor, Diplomacy.Outcome.WAR);
							actor.relations.put(newCiv, Diplomacy.Outcome.WAR);
							return;
						case PHARMACEUTICALS:
							rep.append("The expedition encounters plants with useful pharmaceutical properties. ");
							actor.setScience(actor.getScience() + 4);
							major = true;
							break;
						case SHAPE_SHIFTER:
							if (sg.p(3)) {
								major = true;
								victimP = sg.pick(actor.colonies);
								rep.append("Shape-shifters impersonate the crew of the expedition. Upon their return to ").append(victimP.name).append(" they merge into the population.");
								Agent ag = new Agent(AgentType.SHAPE_SHIFTER, sg.year, "Pack of Shape-Shifters", sg);
								ag.setLocation(victimP);
								sg.agents.add(ag);
							}
							return;
						case ULTRAVORES:
							victimP = sg.pick(actor.colonies);
							if (victimP.population() < 2 || sg.coin()) {
								if (sg.p(10)) {
									rep.append("The expedition captures an ultravore. The science of the ").append(actor.name).append(" fashions it into a living weapon of war. ");
									actor.largestColony().addArtefact(new Artefact(sg.year, actor, ArtefactType.Device.LIVING_WEAPON, ArtefactType.Device.LIVING_WEAPON.create(actor, sg)));
									major = true;
								}
							} else {
								major = true;
								rep.append("An ultravore stows away on the expedition's ship. Upon their return to ").append(victimP.name).append(" it escapes and multiplies.");
								Agent ag = new Agent(AgentType.ULTRAVORES, sg.year, "Hunting Pack of Ultravores", sg);
								ag.setLocation(victimP);
								sg.agents.add(ag);
								return;
							}
							break;
					}
				}
				
				// The locals.
				if (p.getOwner() == null) {
					for (Population pop : new ArrayList<Population>(p.inhabitants)) {
						major = true;
						if (pop.type.base == SentientType.Base.DEEP_DWELLERS) {
							rep.append("They remain unaware of the Deep Dweller culture far beneath. ");
							continue;
						}
						SentientEncounterOutcome seo = sg.pick(actor.getGovt().encounterOutcomes);
						rep.append(seo.desc.replace("$a", pop.type.getName()));
						switch (seo) {
							case EXTERMINATE:
								int kills = sg.d(3) + 1;
								if (kills >= pop.getSize()) {
									p.dePop(pop, sg.year, null, "a campaign of extermination by " + actor.name, null);
									rep.append(" and wipe them out. ");
								} else {
									pop.setSize(pop.getSize() - kills);
									rep.append(", killing ").append(kills).append(" billion.");
								}
								break;
							case EXTERMINATE_FAIL:
								Civ newCiv = new Civ(sg.year, pop.type, p, Government.REPUBLIC, 1, sg.historicalCivNames);
								pop.setSize(pop.getSize() + 1);
								actor.relations.put(newCiv, Diplomacy.Outcome.WAR);
								newCiv.relations.put(actor, Diplomacy.Outcome.WAR);
								sg.civs.add(newCiv);
								sg.historicalCivNames.add(newCiv.name);
								rep.append(", but their campaign fails disastrously. The local ").append(pop.type.getName()).append(" steal their technology and establish themselves as the ").append(newCiv.name).append(".");
								return;
							case GIVE_FULL_MEMBERSHIP:
								if (!actor.fullMembers.contains(pop.type)) {
									actor.fullMembers.add(pop.type);
									actor.updateName(sg.historicalCivNames);
									rep.append(" They now call themselves the ").append(actor.name).append(".");
								}
								// INTENTIONAL FALLTHROUGH!!!
							case SUBJUGATE:
								p.setOwner(actor);
								if (!actor.colonies.contains(p)) { actor.colonies.add(p); }
								break;
						}
						rep.append(" ");
					}
				}
				
				// The strata.
				for (int stratNum = 0; stratNum < p.strata.size(); stratNum++) {
					Stratum stratum = p.strata.get(p.strata.size() - stratNum - 1);
					if (sg.p(4 + stratNum * 2)) {
						if (stratum instanceof Fossil) {
							rep.append("They discover: ").append(stratum.toString()).append(" ");
							actor.setScience(actor.getScience() + 1);
						}
						if (stratum instanceof Remnant) {
							rep.append("They discover: ").append(stratum.toString()).append(" ");
							actor.setResources(actor.getResources() + 1);
							actor.setScience(actor.getScience() + 1);
							Remnant r = (Remnant) stratum;
							Planet homeP = actor.largestColony();
							if (r.plague != null && sg.d(6) < r.plague.transmissivity) {
								boolean affects = false;
								for (Population pop : homeP.inhabitants) {
									if (r.plague.affects.contains(pop.type)) {
										affects = true;
									}
								}
								
								if (affects) {
									homeP.addPlague(new Plague(r.plague));
									rep.append(" Unfortunately, they catch the ").append(r.plague.name).append(" from their exploration of the ancient tombs, infecting ").append(homeP.name).append(" upon their return.");
									major = true;
								}
							}
						}
						if (stratum instanceof Ruin) {
							rep.append("They discover: ").append(stratum.toString()).append(" ");
							Ruin ruin = (Ruin) stratum;
							if (ruin.structure.type instanceof StructureType.Standard) {
								switch ((StructureType.Standard) ruin.structure.type) {
									case MILITARY_BASE:
										actor.setMilitary(actor.getMilitary() + 1);
										actor.setScience(actor.getScience() + 1);
										actor.setResources(actor.getResources() + 1);
										break;
									case SCIENCE_LAB:
										actor.setScience(actor.getScience() + 3);
										break;
									case MINING_BASE:
										actor.setResources(actor.getResources() + 5);
										break;
									default:
										actor.setResources(actor.getResources() + 2);
										break;
								}
							}
						}
						if (stratum instanceof LostArtefact) {
							LostArtefact la = (LostArtefact) stratum;
							if (la.artefact.type == ArtefactType.PIRATE_TOMB || la.artefact.type == ArtefactType.PIRATE_HOARD || la.artefact.type == ArtefactType.ADVENTURER_TOMB) {
								rep.append("They loot the ").append(la.artefact.desc).append(". ");
								actor.setResources(actor.getResources() + la.artefact.specialValue);
								p.strata.remove(stratum);
								stratNum--;
								return;
							}
							if (la.artefact.type == ArtefactType.Device.STASIS_CAPSULE) {
								if (!sg.civs.contains(la.artefact.creator)) {
									rep.append("They open a stasis capsule from the ").append(la.artefact.creator.name).append(", which arises once more!");
									sg.civs.add(la.artefact.creator);
									la.artefact.creator.setTechLevel(la.artefact.creatorTechLevel);
									la.artefact.creator.setResources(10);
									la.artefact.creator.setMilitary(10);
									if (p.getOwner() != null) {
										p.getOwner().relations.put(la.artefact.creator, Diplomacy.Outcome.WAR);
										la.artefact.creator.relations.put(p.getOwner(), Diplomacy.Outcome.WAR);
										p.getOwner().colonies.remove(p);
									}
									la.artefact.creator.colonies.clear();
									la.artefact.creator.colonies.add(p);
									p.setOwner(la.artefact.creator);
									boolean inserted = false;
									for (Population pop : p.inhabitants) {
										if (pop.type == la.artefact.st) {
											pop.setSize(pop.getSize() + 3);
											inserted = true;
											break;
										}
									}
									if (!inserted) {
										new Population(la.artefact.st, 3, p);
									}
									la.artefact.creator.birthYear = sg.year;
									p.strata.remove(stratum);
									stratNum--;
									return;
								}
								continue;
							}
							if (la.artefact.type == ArtefactType.Device.MIND_ARCHIVE) {
								rep.append("They encounter a mind archive of the ").append(la.artefact.creator.name).append(" which brings them new knowledge and wisdom. ");
								major = true;
								actor.setTechLevel(Math.max(actor.getTechLevel(), la.artefact.creatorTechLevel));
								continue;
							}
							if (la.artefact.type == ArtefactType.WRECK) {
								rep.append("They recover: ").append(stratum).append(" ");
								p.strata.remove(stratum);
								actor.setResources(actor.getResources() + 3);
								stratNum--;
								continue;
							}
							
							rep.append("They recover: ").append(stratum).append(" ");
							major = true;
							p.strata.remove(stratum);
							// qqdPS
							sg.pick(actor.colonies).addArtefact(la.artefact);
							stratNum--;
						}
					}
				}
				
				/*if (!major || rep.toString().equals(base)) {
					rep.delete(0, rep.length());
				}*/ // qqDPS Show all reports, even boring ones.
			}
			
			return;
		}
	},
	COLONISE_PLANET() {
		@Override
		public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			if (actor.getResources() < 6) { return; }
			if (actor.population() < 2) { return; }
			Planet srcP = actor.largestColony();
			if (srcP.population() <= 1) { return; }
			for (int tries = 0; tries < 20; tries++) {
				// Pick a planet to colonise.
				Planet p = sg.pick(actor.reachables(sg));
				if (!p.habitable) { continue; }
				if (p.getOwner() != null) { continue; }
				// Who shall the colonists be?
				
				actor.setResources(actor.getResources() - 6);
				p.setOwner(actor);
				actor.colonies.add(p);
				rep.append("The ").append(actor.name).append(" colonise ").append(p.name).append(". ");
				boolean ne = false;
				if (!p.inhabitants.isEmpty()) { rep.append("Of the natives of that planet, "); ne = true; }
				boolean first = true;
				boolean updNeeded = false;
				for (Population nativeP : new ArrayList<Population>(p.inhabitants)) {
					if (!first) {
						rep.append(", ");
						if (p.inhabitants.indexOf(nativeP) == p.inhabitants.size() - 1) {
							rep.append("and ");
						}
					}
					rep.append("the ").append(nativeP);
					
					SentientEncounterOutcome seo = sg.pick(actor.getGovt().encounterOutcomes);
					switch (seo) {
						case EXTERMINATE:
						case EXTERMINATE_FAIL:
							p.dePop(nativeP, sg.year, null, "through the actions of the " + actor.name, null);
							rep.append(" are exterminated");
							break;
						case IGNORE:
						case SUBJUGATE:
							rep.append(" are enslaved");
							nativeP.addUpdateImgs();
							break;
						case GIVE_FULL_MEMBERSHIP:
							rep.append(" are given full membership in the ").append(actor.name);
							if (!actor.fullMembers.contains(nativeP.type)) {
								actor.fullMembers.add(nativeP.type);
								updNeeded = true;
							}
							nativeP.addUpdateImgs();
							break;
					}

					first = false;
				}
				animate();
				if (ne) { rep.append(". "); }
				if (updNeeded) {
					actor.updateName(sg.historicalCivNames);
					rep.append(" They now call themselves the ").append(actor.name).append(".");
				}
				Population srcPop = null;
				for (Population pop : srcP.inhabitants) {
					if (actor.fullMembers.contains(pop.type) && pop.getSize() > 1) {
						srcPop = pop;
					}
				}
				if (srcPop == null) { srcPop = sg.pick(srcP.inhabitants); }
				srcPop.send(p);
				return;
			}
		}
	},
	BUILD_SCIENCE_OUTPOST() {
		@Override
		public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			buildOutpost(StructureType.Standard.SCIENCE_LAB, actor, sg, rep);
		}
	},
	BUILD_MILITARY_BASE() {
		@Override
		public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			buildOutpost(StructureType.Standard.MILITARY_BASE, actor, sg, rep);
		}
	},
	BUILD_MINING_BASE() {
		@Override
		public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			buildOutpost(StructureType.Standard.MINING_BASE, actor, sg, rep);
		}
	},
	DO_RESEARCH() {
		@Override
		public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			if (actor.getResources() == 0) { return; }
			int res = Math.min(actor.getResources(), sg.d(6));
			actor.setResources(actor.getResources() - res);
			actor.setScience(actor.getScience() + res);
		}
	},
	BUILD_WARSHIPS() {
		@Override
		public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			if (actor.getResources() < 3) { return; }
			int res = Math.min(actor.getResources(), sg.d(6) + 2);
			actor.setResources(actor.getResources() - res);
			actor.setMilitary(actor.getMilitary() + res);
			//rep.append("The ").append(actor.name).append(" constructs a fleet of ").append(res).append(" warships.");
		}
	},
	BUILD_CONSTRUCTION() {
		@Override
		public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			buildColonyStructure(sg.pick(StructureType.Standard.COLONY_ONLY), actor, sg, rep);
		}
	};
	
	public void i(Civ actor, SpaceGen sg, StringBuilder rep) { return; }
	public void invoke(Civ actor, SpaceGen sg) {
		StringBuilder rep = new StringBuilder();
		i(actor, sg, rep);
		if (rep.length() > 0) { sg.l(rep.toString()); confirm(); }
	}
	
	void buildOutpost(StructureType st, Civ actor, SpaceGen sg, StringBuilder rep) {
		if (actor.getResources() < 5) { return; }
		for (int tries = 0; tries < 20; tries++) {
			// Pick a planet.
			Planet p = sg.pick(actor.reachables(sg));
			if ((p.getOwner() != null || (!p.inhabitants.isEmpty()) && p.getOwner() != actor)) { continue; }
			if (p.has(st)) { continue; }
			if (p.structures.size() >= 5) { continue; }
			
			Planet srcP = actor.closestColony(p);
			animate(track(srcP.sprite));
			if (p.getOwner() != actor) {
				Sprite expedition = new Sprite(Imager.EXPEDITION, srcP.sprite.x - 48, srcP.sprite.y + 160 / 2 - 32 / 2);
				expedition.children.add(new CivSprite(actor, true));
				animate(add(expedition));
				animate(tracking(expedition, move(expedition, p.sprite.x - 48, p.sprite.y + 160 / 2 - 32 / 2)));
				animate(track(p.sprite), remove(expedition));
			}
			
			if (p.getOwner() != actor) {
				p.setOwner(actor);
				actor.colonies.add(p);
			}
			actor.setResources(actor.getResources() - 5);
			p.addStructure(new Structure(st, actor, sg.year));
			rep.append("The ").append(actor.name).append(" build a ").append(st.getName()).append(" on ").append(p.name).append(".");
			return;
		}
	}
	
	void buildColonyStructure(StructureType st, Civ actor, SpaceGen sg, StringBuilder rep) {
		if (actor.getResources() < 8) { return; }
		if (sg.p(3)) {
			st = sg.pick(sg.pick(actor.fullMembers).specialStructures);
		}
		for (int tries = 0; tries < 20; tries++) {
			// Pick a planet.
			Planet p = sg.pick(actor.colonies);
			if (p.isOutpost()) { continue; }
			if (p.has(st)) { continue; }
			if (p.structures.size() >= 5) { continue; }
			
			animate(track(p.sprite));
			
			if (p.getOwner() != actor) {
				p.setOwner(actor);
				actor.colonies.add(p);
			}
			actor.setResources(actor.getResources() - 8);
			p.addStructure(new Structure(st, actor, sg.year));
			rep.append("The ").append(actor.name).append(" build a ").append(st.getName()).append(" on ").append(p.name).append(".");
			actor.decrepitude -= 3;
			return;
		}
	}
}
