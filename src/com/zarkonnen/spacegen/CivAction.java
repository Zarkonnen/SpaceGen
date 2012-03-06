package com.zarkonnen.spacegen;

import java.util.ArrayList;
import java.util.HashMap;

public enum CivAction {
	EXPLORE_PLANET() {
		@Override
		public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			// Pick a planet to explore.
			Planet p = sg.pick(actor.reachables(sg));
			if (p.owner != null && p.owner != actor) {
				// They meet a civ.
				rep.append("The ").append(actor.name).append(" send a delegation to ").append(p.name).append(". ");
				Diplomacy.Outcome outcome = Diplomacy.meet(actor, p.owner, sg);
				rep.append(outcome.desc(p.owner, actor.relation(p.owner))).append(" ");
				if (outcome == Diplomacy.Outcome.UNION) {
					sg.civs.remove(p.owner);
					Government newGovt = sg.pick(new Government[] { actor.govt, p.owner.govt });
					actor.govt = newGovt;
					actor.colonies.addAll(p.owner.colonies);
					actor.resources += p.owner.resources;
					actor.science += p.owner.science;
					for (SentientType st : p.owner.fullMembers) {
						if (!actor.fullMembers.contains(st)) {
							actor.fullMembers.add(st);
						}
					}
					for (Planet c : p.owner.colonies) {
						c.owner = actor;
					}
					HashMap<Civ, Diplomacy.Outcome> newRels = new HashMap<Civ, Diplomacy.Outcome>();
					for (Civ c : sg.civs) {
						if (c == actor || c == p.owner) { continue; }
						if (actor.relation(c) == Diplomacy.Outcome.WAR ||
							p.owner.relation(c) == Diplomacy.Outcome.WAR)
						{
							newRels.put(c, Diplomacy.Outcome.WAR);
							c.relations.put(actor, Diplomacy.Outcome.WAR);
						} else {
							newRels.put(c, Diplomacy.Outcome.PEACE);
							c.relations.put(actor, Diplomacy.Outcome.PEACE);
						}
					}
					actor.relations = newRels;
					actor.updateName(sg.historicalCivs);
					rep.append("The two civilizations combine into the ").append(actor.name).append(". ");
					return;
				} else {
					if (actor.relation(p.owner) != outcome) {
						actor.relations.put(p.owner, outcome);
						p.owner.relations.put(actor, outcome);
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
							victimP = sg.pick(actor.colonies);
							int stolenResources = actor.resources / actor.colonies.size();
							Civ newCiv = new Civ(sg.year, SentientType.PARASITES, victimP, sg.pick(Government.values()), stolenResources, sg.historicalCivs);
							rep.append("The expedition encounters brain parasites. Upon their return to ").append(victimP.name).append(", the parasites take over the brains of the planet's inhabitants, creating the ").append(newCiv.name).append(".");
							victimP.owner = newCiv;
							actor.colonies.remove(victimP);
							sg.civs.add(newCiv);
							sg.historicalCivs.add(newCiv);
							newCiv.relations.put(actor, Diplomacy.Outcome.WAR);
							actor.relations.put(newCiv, Diplomacy.Outcome.WAR);
							return;
						case PHARMACEUTICALS:
							rep.append("The expedition encounters plants with useful pharmaceutical properties. ");
							actor.science += 4;
							major = true;
							break;
						case SHAPE_SHIFTER:
							// todo, needs wandering monsters
							break;
						case ULTRAVORE:
							victimP = sg.pick(actor.colonies);
							if (victimP.population() < 2 || sg.p(3)) {
								if (sg.p(6)) {
									rep.append("The expedition captures an ultravore. The science of the ").append(actor.name).append(" fashions it into a living weapon of war. ");
									actor.largestColony().artefacts.add(new Artefact(sg.year, actor, ArtefactType.Device.LIVING_WEAPON, ArtefactType.Device.LIVING_WEAPON.create(actor, sg)));
									major = true;
								}
							} else {
								Population victimPop = sg.pick(victimP.inhabitants);
								if (victimPop.size == 1) {
									victimP.dePop(victimPop, sg.year, null, "predation by an ultravore", null);
								} else {
									victimPop.size--;
								}
								rep.append("An ultravore stows away on the expedition's ship. Upon their return to ").append(victimP.name).append(" it escapes and multiplies, killing a billion ").append(victimPop.type.name).append(". ");
								return;
							}
							break;
					}
				}
				
				// The locals.
				if (p.owner == null) {
					for (Population pop : new ArrayList<Population>(p.inhabitants)) {
						major = true;
						if (pop.type == SentientType.DEEP_DWELLERS) {
							rep.append("They remain unaware of the Deep Dweller culture far beneath. ");
							continue;
						}
						SentientEncounterOutcome seo = sg.pick(actor.govt.encounterOutcomes);
						rep.append(seo.desc.replace("$a", pop.type.name));
						switch (seo) {
							case EXTERMINATE:
								int kills = sg.d(3) + 1;
								if (kills >= pop.size) {
									p.dePop(pop, sg.year, null, "a campaign of extermination by " + actor.name, null);
									rep.append(" and wipe them out. ");
								} else {
									pop.size -= kills;
									rep.append(", killing ").append(kills).append(" billion. ");
								}
								break;
							case EXTERMINATE_FAIL:
								Civ newCiv = new Civ(sg.year, pop.type, p, Government.REPUBLIC, 1, sg.historicalCivs);
								pop.size++;
								actor.relations.put(newCiv, Diplomacy.Outcome.WAR);
								newCiv.relations.put(actor, Diplomacy.Outcome.WAR);
								sg.civs.add(newCiv);
								sg.historicalCivs.add(newCiv);
								rep.append(", but their campaign fails disastrously. The local ").append(pop.type.name).append(" steal their technology and establish themselves as the ").append(newCiv.name).append(".");
								return;
							case GIVE_FULL_MEMBERSHIP:
								if (!actor.fullMembers.contains(pop.type)) {
									actor.fullMembers.add(pop.type);
								}
								actor.updateName(sg.historicalCivs);
								// INTENTIONAL FALLTHROUGH!!!
							case SUBJUGATE:
								p.owner = actor;
								if (!actor.colonies.contains(p)) { actor.colonies.add(p); }
								break;
						}
					}
				}
				
				// The strata.
				for (int stratNum = 0; stratNum < p.strata.size(); stratNum++) {
					Stratum stratum = p.strata.get(p.strata.size() - stratNum - 1);
					if (sg.p(4 + stratNum * 2)) {
						if (stratum instanceof Fossil) {
							rep.append("They discover: ").append(stratum.toString()).append(" ");
							actor.science++;
						}
						if (stratum instanceof Remnant) {
							rep.append("They discover: ").append(stratum.toString()).append(" ");
							actor.resources++;
							actor.science++;
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
									homeP.plagues.add(new Plague(r.plague));
									rep.append(" Unfortunately, they catch the ").append(r.plague.name).append(" from their exploration of the ancient tombs, infecting ").append(homeP.name).append(" upon their return.");
									major = true;
								}
							}
						}
						if (stratum instanceof Ruin) {
							rep.append("They discover: ").append(stratum.toString()).append(" ");
							Ruin ruin = (Ruin) stratum;
							switch (ruin.structure.type) {
								case MILITARY_BASE:
									actor.military++;
									actor.science++;
									actor.resources++;
									break;
								case SCIENCE_LAB:
									actor.science += 3;
									break;
								case MINING_BASE:
									actor.resources += 5;
									break;
							}
						}
						if (stratum instanceof LostArtefact) {
							LostArtefact la = (LostArtefact) stratum;
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
									return;
								}
								continue;
							}
							if (la.artefact.type == ArtefactType.Device.MIND_ARCHIVE) {
								rep.append("They encounter a mind archive of the ").append(la.artefact.creator.name).append(" which brings them new knowledge and wisdom. ");
								major = true;
								actor.techLevel = Math.max(actor.techLevel, la.artefact.creatorTechLevel);
								continue;
							}
							
							rep.append("They recover: ").append(stratum).append(" ");
							major = true;
							p.strata.remove(stratum);
							sg.pick(actor.colonies).artefacts.add(la.artefact);
							stratNum--;
						}
					}
				}
				
				if (!major || rep.toString().equals(base)) {
					rep.delete(0, rep.length());
				}
			}
			
			return;
		}
	},
	COLONISE_PLANET() {
		@Override
		public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			if (actor.resources < 6) { return; }
			if (actor.population() < 2) { return; }
			Planet srcP = actor.largestColony();
			if (srcP.population() <= 1) { return; }
			for (int tries = 0; tries < 20; tries++) {
				// Pick a planet to colonise.
				Planet p = sg.pick(actor.reachables(sg));
				if (!p.habitable) { continue; }
				if (p.owner != null) { continue; }
				// Who shall the colonists be?
				
				rep.append("The ").append(actor.name).append(" colonise ").append(p.name).append(". ");
				boolean ne = false;
				if (!p.inhabitants.isEmpty()) { rep.append("Of the natives of that planet, "); ne = true; }
				boolean first = true;
				for (Population nativeP : new ArrayList<Population>(p.inhabitants)) {
					if (!first) {
						rep.append(", ");
						if (p.inhabitants.indexOf(nativeP) == p.inhabitants.size() - 1) {
							rep.append("and ");
						}
					}
					rep.append("the ").append(nativeP);
					SentientEncounterOutcome seo = sg.pick(actor.govt.encounterOutcomes);
					switch (seo) {
						case EXTERMINATE:
						case EXTERMINATE_FAIL:
							p.dePop(nativeP, sg.year, null, "through the actions of the " + actor.name, null);
							rep.append(" are exterminated");
							break;
						case IGNORE:
						case SUBJUGATE:
							rep.append(" are enslaved");
							break;
						case GIVE_FULL_MEMBERSHIP:
							rep.append(" are given full membership in the ").append(actor.name);
							if (!actor.fullMembers.contains(nativeP.type)) {
								actor.fullMembers.add(nativeP.type);
							}
							break;
					}

					actor.updateName(sg.historicalCivs);

					first = false;
				}
				if (ne) { rep.append(". "); }
				actor.resources -= 6;
				p.owner = actor;
				actor.colonies.add(p);
				Population srcPop = null;
				for (Population pop : srcP.inhabitants) {
					if (actor.fullMembers.contains(pop.type) && pop.size > 1) {
						srcPop = pop;
					}
				}
				if (srcPop == null) { srcPop = sg.pick(srcP.inhabitants); }
				if (srcPop.size == 1) {
					srcP.inhabitants.remove(srcPop);
				}
				boolean inserted = false;
				for (Population pop : p.inhabitants) {
					if (pop.type == srcPop.type) {
						pop.size++;
						inserted = true;
						break;
					}
				}
				if (!inserted) { p.inhabitants.add(new Population(srcPop.type, 1)); }
				return;
			}
		}
	},
	BUILD_SCIENCE_OUTPOST() {
		@Override
		public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			buildOutpost(StructureType.SCIENCE_LAB, actor, sg, rep);
		}
	},
	BUILD_MILITARY_BASE() {
		@Override
		public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			buildOutpost(StructureType.MILITARY_BASE, actor, sg, rep);
		}
	},
	BUILD_MINING_BASE() {
		@Override
		public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			buildOutpost(StructureType.MINING_BASE, actor, sg, rep);
		}
	},
	DO_RESEARCH() {
		@Override
		public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			if (actor.resources == 0) { return; }
			int res = Math.min(actor.resources, sg.d(6));
			actor.resources -= res;
			actor.science += res;
		}
	},
	BUILD_WARSHIPS() {
		@Override
		public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			if (actor.resources < 3) { return; }
			int res = Math.min(actor.resources, sg.d(6) + 2);
			actor.resources -= res;
			actor.military += res;
			//rep.append("The ").append(actor.name).append(" constructs a fleet of ").append(res).append(" warships.");
		}
	},
	BUILD_CONSTRUCTION() {
		@Override
		public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			buildColonyStructure(sg.pick(StructureType.COLONY_ONLY), actor, sg, rep);
		}
	};
	
	public void i(Civ actor, SpaceGen sg, StringBuilder rep) { return; }
	public void invoke(Civ actor, SpaceGen sg) {
		StringBuilder rep = new StringBuilder();
		i(actor, sg, rep);
		if (rep.length() > 0) { sg.l(rep.toString()); }
	}
	
	void buildOutpost(StructureType st, Civ actor, SpaceGen sg, StringBuilder rep) {
		if (actor.resources < 5) { return; }
		for (int tries = 0; tries < 20; tries++) {
			// Pick a planet.
			Planet p = sg.pick(actor.reachables(sg));
			if ((p.owner != null || !p.inhabitants.isEmpty()) && p.owner != actor) { continue; }
			
			if (p.has(st)) { continue; }
			if (p.owner != actor) {
				p.owner = actor;
				actor.colonies.add(p);
			}
			actor.resources -= 5;
			p.structures.add(new Structure(st, actor, sg.year));
			//rep.append("The ").append(actor.name).append(" build a ").append(st.name).append(" on ").append(p.name).append(".");
			return;
		}
	}
	
	void buildColonyStructure(StructureType st, Civ actor, SpaceGen sg, StringBuilder rep) {
		if (actor.resources < 8) { return; }
		for (int tries = 0; tries < 20; tries++) {
			// Pick a planet.
			Planet p = sg.pick(actor.colonies);
			if (p.isOutpost()) { continue; }
			if (p.has(st)) { continue; }
			if (p.owner != actor) {
				p.owner = actor;
				actor.colonies.add(p);
			}
			actor.resources -= 8;
			p.structures.add(new Structure(st, actor, sg.year));
			rep.append("The ").append(actor.name).append(" build a ").append(st.name).append(" on ").append(p.name).append(".");
			return;
		}
	}
}
