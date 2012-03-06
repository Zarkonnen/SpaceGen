/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zarkonnen.spacegen;

import java.util.ArrayList;
import java.util.Random;

public class SpaceGen {
	Random r;
	ArrayList<String> log = new ArrayList<String>();
	ArrayList<Planet> planets = new ArrayList<Planet>();
	ArrayList<Civ> civs = new ArrayList<Civ>();
	ArrayList<Civ> historicalCivs = new ArrayList<Civ>();
	boolean hadCivs = false;
	boolean yearAnnounced = false;
	int year = 0;
	int age = 1;
	
	public static void main(String[] args) {
		SpaceGen sg = new SpaceGen(args.length > 1 ? Long.parseLong(args[1]) : System.currentTimeMillis());
		int ticks = args.length > 0 ? Integer.parseInt(args[0]) : 300;
		for (int t = 0; t < ticks; t++) {
			sg.tick();
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
			Planet p = new Planet(r);
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
			if ((planet.population() > 12 || (planet.population() > 7 && p(10)) && planet.pollution < 4)) {
				planet.pollution++;
			}
			for (Population pop : new ArrayList<Population>(planet.inhabitants)) {
				int roll = d(6);
				if (roll < planet.pollution) {
					//l("Pollution kills a billion $sname on $pname.", pop.type, planet);
					planet.pollution--;
					pop.size--;
				} else {
					if (roll == 6 || (pop.type == SentientType.ANTOIDS && roll > 2) || (planet.owner != null && roll == 5)) {
						pop.size++;
						//l("The population of $sname on $pname has grown by a billion.", pop.type, planet);
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
						if (d(20) < plague.mutationRate) {
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
			int newRes = 1;
			int newSci = 1;
			for (Planet col : new ArrayList<Planet>(c.colonies)) {
				if (col.population() > 7 || (col.population() > 4 && p(3))) {
					col.evoPoints = 0;
					col.pollution++;
					//l("Overcrowding on $name leads to increased pollution.", col);
				}
				if (col.population() == 0 && !col.isOutpost()) {
					col.deCiv(year, null, "");
				} else {
					if (col.population() > 0) {
						newRes++;
						if (col.lifeforms.contains(SpecialLifeform.VAST_HERDS)) { newRes++; }
					}
					if (col.specials.contains(PlanetSpecial.GEM_WORLD)) { newRes++; }
					if (col.has(StructureType.MINING_BASE)) { newRes += 2; }
					if (col.has(StructureType.SCIENCE_LAB)) { newSci += 2; }
				}
			}
			
			if (checkCivDoom(c)) { civs.remove(c); continue; }
			
			c.resources += newRes;
			
			SentientType lead = pick(c.fullMembers);
			pick(lead.behaviour).invoke(c, this);
			if (checkCivDoom(c)) { civs.remove(c); continue; }
			pick(c.govt.behaviour).invoke(c, this);
			if (checkCivDoom(c)) { civs.remove(c); continue; }
			
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
				if (checkCivDoom(c)) { civs.remove(c); continue; }
			}
			
			War.doWar(c, this);
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
							l("The $sname on $pname achieve spaceflight and organise as a " + g.typeName + "!", starter.type, p);
							Civ c = new Civ(year, starter.type, p, g, d(3), historicalCivs);
							historicalCivs.add(c);
							civs.add(c);
							p.owner = c;
						}
					} else {
						if (p(3)) {
							// Sentient!
							SentientType st = pick(SentientType.values());
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
				if (p(5000 / sAge + 600)) {
					p.strata.remove(s);
				}
			}
		}
		
		for (Planet p : planets) {
			if (p.owner != null) {
				if (!p.owner.colonies.contains(p)) {
					System.out.println("OMG BBQ WTF");
				}
				if (!civs.contains(p.owner)) {
					System.out.println("OMG BBQ WTF");
				}
			}
		}
	}
	
	public void describe() {
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
		l(s.replace("$name", st.name));
	}
	
	final void l(String s, Civ st) {
		l(s.replace("$name", st.name));
	}
	
	final void l(String s, Civ st, Planet p) {
		l(s.replace("$cname", st.name).replace("$pname", p.name));
	}
	
	final void l(String s, SentientType st, Planet p) {
		l(s.replace("$sname", st.name).replace("$pname", p.name));
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
