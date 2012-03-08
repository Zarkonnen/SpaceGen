package com.zarkonnen.spacegen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class SpaceGen {
	Random r;
	ArrayList<String> log = new ArrayList<String>();
	ArrayList<Planet> planets = new ArrayList<Planet>();
	ArrayList<Civ> civs = new ArrayList<Civ>();
	ArrayList<String> historicalCivNames = new ArrayList<String>();
	ArrayList<Agent> agents = new ArrayList<Agent>();
	ArrayList<String> historicalSentientNames = new ArrayList<String>();
	boolean hadCivs = false;
	boolean yearAnnounced = false;
	int year = 0;
	int age = 1;
	
	public static void main(String[] args) {
		SpaceGen sg = new SpaceGen(args.length > 1 ? Long.parseLong(args[1]) : System.currentTimeMillis());
		int ticks = args.length > 0 ? Integer.parseInt(args[0]) : sg.r.nextInt(400) + 120;
		int wait = 0;
		for (int t = 0; t < ticks; t++) {
			sg.tick();
			if (sg.civs.size() >= 3) {
				wait++;
				if (wait > 20) {
					break;
				}
			} else {
				wait = 0;
			}
		}
		sg.l("");
		sg.l("");
		sg.describe();
	}

	public SpaceGen(long seed) {
		l("IN THE BEGINNING, ALL WAS DARK.");
		l("THEN, PLANETS BEGAN TO FORM:");
		r = new Random(seed);
		int np = 6 + d(4, 6);
		for (int i = 0; i < np; i++) {
			Planet p = new Planet(r, this);
			l(p.name);
			planets.add(p);
		}
	}
	
	public boolean checkCivDoom(Civ c) {
		if (c.fullColonies().isEmpty()) {
			l("The $name collapses.", c);
			for (Planet out : new ArrayList<Planet>(c.colonies)) {
				out.deCiv(year, null, "during the collapse of the " + c.name);
			}
			return true;
		}
		if (c.colonies.size() == 1 && c.colonies.get(0).population() == 1) {
			Planet remnant = c.colonies.get(0);
			l("The $cname collapses, leaving only a few survivors on $pname.", c, remnant);
			remnant.owner = null;
			c.colonies.clear();
			return true;
		}
		return false;
	}
	
	public void tick() {
		year++;
		yearAnnounced = false;
		if (!hadCivs && !civs.isEmpty()) {
			l("WE ENTER THE " + Names.nth(age).toUpperCase() + " AGE OF CIVILISATION");
		}
		if (hadCivs && civs.isEmpty()) {
			age++;
			l("WE ENTER THE " + Names.nth(age).toUpperCase() + " AGE OF DARKNESS");
		}
		hadCivs = !civs.isEmpty();
		
		planets: for (Planet planet : planets) {
			if (p(1000)) {
				String mName = "giant spaceborne " + pick(Names.COLORS).toLowerCase() + " " + pick(AgentType.MONSTER_TYPES);
				l("A " + mName + " appears from the depths of space and menaces the skies of $name.", planet);
				Agent m = new Agent(AgentType.SPACE_MONSTER, year, mName);
				m.p = planet;
				agents.add(m);
			}
			
			if ((planet.population() > 12 || (planet.population() > 7 && p(10)) && planet.pollution < 4)) {
				planet.pollution++;
			}
			for (Population pop : new ArrayList<Population>(planet.inhabitants)) {
				if (planet.owner == null && p(100) && pop.type.base != SentientType.Base.ROBOTS && pop.type.base != SentientType.Base.PARASITES) {
					SentientType nst = pop.type.mutate(this, null);
					l("The $sname on $pname mutate into " + nst.getName() + ".", pop.type, planet);
					pop.type = nst;
				}
				int roll = d(6);
				if (roll < planet.pollution) {
					//l("Pollution kills a billion $sname on $pname.", pop.type, planet);
					planet.pollution--;
					pop.size--;
				} else {
					if (roll == 6 || (pop.type.base == SentientType.Base.ANTOIDS && roll > 3) || (planet.owner != null && roll == 5) ||
						(planet.has(SentientType.Base.ANTOIDS.specialStructure) && roll > 2))
					{
						pop.size++;
						//l("The population of $sname on $pname has grown by a billion.", pop.type, planet);
					}
				}
				if (pop.type.base == SentientType.Base.KOBOLDOIDS && p(10) && planet.has(SentientType.Base.KOBOLDOIDS.specialStructure)) {
					pop.size++;
					l("The skull pile on $pname excites the local $sname into a sexual frenzy.", pop.type, planet);
				}
				if (pop.size > 3 && pop.type.base == SentientType.Base.KOBOLDOIDS && p(8)) {
					l("The $sname on $pname devour one billion of their own kind in a mad frenzy of cannibalism!", pop.type, planet);
					if (p(3) && planet.owner != null) {
						l("The $sname erect a pile of skulls on $pname!", pop.type, planet);
						planet.structures.add(new Structure(StructureType.Standard.SKULL_PILE, planet.owner, year));
					}
				}
				if (pop.size <= 0) {
					planet.dePop(pop, year, null, "from the effects of pollution", null);
					l("$sname have died out on $pname!", pop.type, planet);
					continue planets;
				}
				for (Plague plague : new ArrayList<Plague>(planet.plagues)) {
					if (plague.affects.contains(pop.type)) {
						if (d(12) < plague.lethality) {
							pop.size--;
							if (pop.size <= 0) {
								planet.dePop(pop, year, null, "from the " + plague.name, new Plague(plague));
								l("The $sname on $pname have been wiped out by the " + plague.name + "!", pop.type, planet);
							}
						}
					} else {
						if (d(12) < plague.mutationRate && pop.type.base != SentientType.Base.ROBOTS) {
							plague.affects.add(pop.type);
							l("The " + plague.name + " mutates to affect $name", pop.type);
						}
					}
				}
			}
			
			for (Plague plague : new ArrayList<Plague>(planet.plagues)) {
				if (d(12) < plague.curability) {
					planet.plagues.remove(plague);
					l(plague.name + " has been eradicated on $name.", planet);
				} else {
					if (d(12) < plague.transmissivity) {
						Planet target = pick(planets);
						boolean canJump = false;
						for (Population pop : target.inhabitants) {
							if (plague.affects.contains(pop.type)) {
								canJump = true;
							}
						}
						if (canJump) {
							boolean match = false;
							for (Plague p2 : target.plagues) {
								if (p2.name.equals(plague.name)) {
									for (SentientType st : plague.affects) {
										if (!p2.affects.contains(st)) { p2.affects.add(st); }
										match = true;
									}
								}
							}
							if (!match) {
								target.plagues.add(new Plague(plague));
							}
						}
					}
				}
			}
		}
		
		// TICK CIVS
		for (Civ c : new ArrayList<Civ>(civs)) {
			if (checkCivDoom(c)) { civs.remove(c); continue; }
			int newRes = 0;
			int newSci = 1;
			for (Planet col : new ArrayList<Planet>(c.colonies)) {
				if (c.has(ArtefactType.Device.UNIVERSAL_ANTIDOTE)) {
					for (Plague p : col.plagues) {
						l("The " + p.name + " on $name is cured by the universal antidote.", col);
					}
					col.plagues.clear();
				}
				if (col.population() > 7 || (col.population() > 4 && p(3))) {
					col.evoPoints = 0;
					col.pollution++;
					//l("Overcrowding on $name leads to increased pollution.", col);
				}
				if (c.has(ArtefactType.Device.MIND_READER) && p(4)) {
					for (Population pop : col.inhabitants) { 
						pop.size++;
					}
				}
				if (col.population() == 0 && !col.isOutpost()) {
					col.deCiv(year, null, "");
				} else {
					if (col.population() > 0) {
						newRes++;
						if (col.lifeforms.contains(SpecialLifeform.VAST_HERDS)) { newRes++; }
					}
					if (col.specials.contains(PlanetSpecial.GEM_WORLD)) { newRes++; }
					if (col.has(StructureType.Standard.MINING_BASE)) { newRes += 1; }
					if (col.has(StructureType.Standard.SCIENCE_LAB)) { newSci += 2; }
					if (col.has(SentientType.Base.PARASITES.specialStructure)) { newSci += 2; }
					if (col.has(SentientType.Base.TROLLOIDS.specialStructure)) { newSci += 2; }
				}
			}
			
			if (checkCivDoom(c)) { civs.remove(c); continue; }
			
			if (c.has(ArtefactType.Device.MASTER_COMPUTER)) {
				newRes += 2;
				newSci += 3;
			}
			
			c.resources += newRes;
			
			SentientType lead = pick(c.fullMembers);
			pick(lead.base.behaviour).invoke(c, this);
			if (checkCivDoom(c)) { civs.remove(c); continue; }
			pick(c.govt.behaviour).invoke(c, this);
			if (checkCivDoom(c)) { civs.remove(c); continue; }
			
			c.science += newSci;
			
			if (c.science > c.nextBreakthrough) {
				if (Science.advance(c, this)) { continue; }
				c.science -= c.nextBreakthrough;
				c.nextBreakthrough *= 2;
			}
			
			if (p(3)) {
				int evtTypeRoll = d(6);
				boolean good;
				boolean bad;
				int civAge = year - c.birthYear;
				if (civAge < 5) {
					good = evtTypeRoll <= 5;
					bad = false;
				} else if (civAge < 17) {
					good = evtTypeRoll >= 4;
					bad = evtTypeRoll == 1;
				} else if (civAge < 25) {
					good = evtTypeRoll == 6;
					bad = evtTypeRoll < 3;
				} else {
					good = evtTypeRoll == 6;
					bad = evtTypeRoll < 5;
				}

				if (good) {
					pick(GoodCivEvent.values()).invoke(c, this);
				}
				if (checkCivDoom(c)) { civs.remove(c); continue; }
				if (bad) {
					pick(BadCivEvent.values()).invoke(c, this);
				}
				
				for (SentientType st : c.fullMembers) {
					if (st.base == SentientType.Base.HUMANOIDS && p(8)) {
						GoodCivEvent.SPAWN_ADVENTURER.invoke(c, this);
					}
				}
				
				
				if (checkCivDoom(c)) { civs.remove(c); continue; }
			}
			
			War.doWar(c, this);
		}
		
		
		// TICK AGENTS
		for (Agent a : new ArrayList<Agent>(agents)) {
			a.type.behave(a, this);
		}
		
		
		// TICK PLANETS
		for (Planet p : planets) {
			if (p.habitable && p(500)) {
				Cataclysm c = pick(Cataclysm.values());
				Civ civ = p.owner;
				l(c.desc, p);
				p.deLive(year, c, null);
				
				if (civ != null) {
					if (checkCivDoom(civ)) { civs.remove(civ); }
				}
				continue;
			}
			
			if (p(200) && p.pollution > 1 && !p.specials.contains(PlanetSpecial.POISON_WORLD)) {
				l("Pollution on $name abates.", p);
				p.pollution--;
			}
			
			if (p(200 + 5000 * p.specials.size())) {
				PlanetSpecial ps = pick(PlanetSpecial.values());
				if (!p.specials.contains(ps)) {
					p.specials.add(ps);
					ps.apply(p);
					l(ps.announcement, p);
				}
			}
			p.evoPoints += d(6) * d(6) * d(6) * d(6) * d(6) * d(6) * (6 - p.pollution);
			if (p.evoPoints > p.evoNeeded && p(30) && p.pollution < 2) {
				p.evoPoints -= p.evoNeeded;
				if (!p.habitable) {
					p.habitable = true;
					l("Life arises on $name", p);
				} else {
					if (!p.inhabitants.isEmpty()) {
						if (p.owner == null) {
							// Do the civ thing.
							Government g = pick(Government.values());
							Population starter = pick(p.inhabitants);
							starter.size++;
							Civ c = new Civ(year, starter.type, p, g, d(3), historicalCivNames);
							l("The $sname on $pname achieve spaceflight and organise as a " + g.typeName + ", the " + c.name + ".", starter.type, p);
							historicalCivNames.add(c.name);
							civs.add(c);
							p.owner = c;
						}
					} else {
						if (p(3)) {
							// Sentient!
							SentientType st = SentientType.invent(this, null, p, null);
							l("Sentient $sname arise on $pname.", st, p);
							p.inhabitants.add(new Population(st, 2 + d(1)));
						} else {
							// Some special creature.
							SpecialLifeform slf = pick(SpecialLifeform.values());
							if (!p.lifeforms.contains(slf)) {
								l("$lname evolve on $pname.", slf, p);
								p.lifeforms.add(slf);
							}
						}
					}
				}
			}
		}
		
		// Erosion
		for (Planet p : planets) {
			for (Stratum s : new ArrayList<Stratum>(p.strata)) {
				int sAge = year - s.time() + 1;
				if (s instanceof Fossil) {
					if (p(12000 / sAge + 800)) {
						p.strata.remove(s);
					}
				}
				if (s instanceof LostArtefact) {
					if (((LostArtefact) s).artefact.type == ArtefactType.Device.STASIS_CAPSULE) { continue; }
					if (p(10000 / sAge + 500)) {
						p.strata.remove(s);
					}
				}
				if (s instanceof Remnant) {
					if (p(4000 / sAge + 400)) {
						p.strata.remove(s);
					}
				}
				if (s instanceof Ruin) {
					Ruin ruin = (Ruin) s;
					if (ruin.structure.type == StructureType.Standard.MILITARY_BASE ||
						ruin.structure.type == StructureType.Standard.MINING_BASE ||
						ruin.structure.type == StructureType.Standard.SCIENCE_LAB)
					{
						if (p(1000 / sAge + 150)) {
							p.strata.remove(s);
						}
					} else {
						if (p(3000 / sAge + 300)) {
							p.strata.remove(s);
						}
					}
				}
			}
		}
		
		for (Planet p : planets) {
			if (p.owner != null) {
				if (!p.owner.colonies.contains(p)) {
					System.out.println("OMG BBQ WTF A");
				}
				if (!civs.contains(p.owner)) {
					System.out.println("OMG BBQ WTF B");
				}
			}
		}
		for (Civ c : civs) {
			for (Planet col : c.colonies) {
				if (col.owner != c) {
					System.out.println("OMG BBQ WTF C");
				}
			}
		}
	}
	
	public void describe() {
		// Critters
		HashSet<SentientType> sts = new HashSet<SentientType>();
		for (Planet p : planets) { for (Population pop : p.inhabitants) {
			sts.add(pop.type);
		}}
		
		if (sts.size() > 0) { l("SENTIENT SPECIES:"); }
		
		for (SentientType st : sts) {
			l(st.getName() + ": " + st.getDesc());
			l("");
		}
		
		if (civs.size() > 0) { l("CIVILISATIONS:"); }
		for (Civ c : civs) {
			l(c.fullDesc(this));
			l("");
		}
		
		l("PLANETS:");
		for (Planet p : planets) {
			l(p.fullDesc());
			l("");
		}
	}
	
	final <T> T pick(ArrayList<T> ts) {
		return ts.get(r.nextInt(ts.size()));
	}
	
	final <T> T pick(T[] ts) {
		return ts[r.nextInt(ts.length)];
	}
	
	final void l(String s, Planet p) {
		l(s.replace("$name", p.name));
	}
	
	final void l(String s, SentientType st) {
		l(s.replace("$name", st.getName()));
	}
	
	final void l(String s, Civ st) {
		l(s.replace("$name", st.name));
	}
	
	final void l(String s, Civ st, Planet p) {
		l(s.replace("$cname", st.name).replace("$pname", p.name));
	}
	
	final void l(String s, SentientType st, Planet p) {
		l(s.replace("$sname", st.getName()).replace("$pname", p.name));
	}
	
	final void l(String s, SpecialLifeform slf, Planet p) {
		l(s.replace("$lname", slf.name).replace("$pname", p.name));
	}
	
	final void l(String s) {
		if (!yearAnnounced) {
			yearAnnounced = true;
			l(year + ":");
		}
		System.out.println(s);
		log.add(s);
	}
	
	final boolean coin() { return r.nextBoolean(); }
	final boolean p(int n) { return d(n) == 0; }
	final boolean atLeast(int requirement, int n) { return requirement >= d(n); }
	final boolean lessThan(int tooMuch, int n) { return tooMuch <= d(n); }
	
	final int d(int n) {
		return r.nextInt(n);
	}
	
	final int d(int rolls, int n) {
		int sum = 0;
		for (int roll = 0; roll < rolls; roll++) { sum += d(n); }
		return sum;
	}
}
