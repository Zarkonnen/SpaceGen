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
		int ticks = args.length > 0 ? Integer.parseInt(args[0]) : 1000;
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
		int np = 5 + d(3, 6);
		for (int i = 0; i < np; i++) {
			Planet p = new Planet(r);
			l(p.name);
			planets.add(p);
		}
	}
	
	public boolean checkCivDoom(Civ c) {
		if (c.colonies.isEmpty()) {
			l("The $name collapses.", c);
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
		
		// TICK CIVS
		for (Civ c : new ArrayList<Civ>(civs)) {
			/*
			 * A. Pollution: add a pollution marker on each colony with a population larger than 5.
B. Population growth/shrinkage: Roll a d6 for each colony. Add one population on a roll of 5 or 6. Remove one population if the roll is less than the amount of pollution.
C. Gather resources: gain 1 resource per colony and 2 per mining outpost.
D. Perform civ activity as indicated by sentient type. (If funds available.)
E. Perform civ activity as indicated by government. (If funds available.)
F. Add research points from science outposts. If the number of points is greater than or equal to 12, roll on the research results table.
G. Roll on the events table as indicated by the civilisation's size.
H. If at war, launch an attack against each enemy. Roll a d6. On a 5 or 6, re-encounter the enemy, which may result in peace.
I. If a civilisation is reduced to population 1, it gets downgraded to a sentient.
			 */
			int newRes = 1;
			int newSci = 1;
			for (Planet col : new ArrayList<Planet>(c.colonies)) {
				if (col.population() > 6) {
					col.pollution++;
					l("Overcrowding on $name leads to increased pollution.", col);
				}
				for (Population p : new ArrayList<Population>(col.inhabitants)) {
					int roll = d(6);
					if (roll < col.pollution) {
						l("Pollution kills a billion $sname on $pname.", p.type, col);
						p.size--;
					} else {
						if (roll == 6 || p.type == SentientType.ANTOIDS) {
							p.size++;
							l("The population of $sname on $pname has grown by a billion.", p.type, col);
						}
					}
					if (p.size <= 0) {
						col.dePop(p, year, null);
						l("$sname have died out on $pname!", p.type, col);
					}
				}
				if (col.population() == 0 && !col.isOutpost()) {
					col.deCiv(year, null);
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
			
			SentientType lead = pick(c.fullMembers);
			pick(lead.behaviour).invoke(c, this);
			if (checkCivDoom(c)) { civs.remove(c); continue; }
		}
		
		
		// TICK PLANETS
		for (Planet p : planets) {
			if (p.habitable && p(500)) {
				Cataclysm c = pick(Cataclysm.values());
				Civ civ = p.owner;
				l(c.desc, p);
				p.deLive(year, c);
				
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
			p.evoPoints += d(6) * d(6) * d(6) * d(6) * d(6) * d(6);
			if (p.evoPoints > 12000 && p(100)) {
				p.evoPoints -= 12000;
				if (!p.habitable) {
					p.habitable = true;
					l("Life arises on $name", p);
				} else {
					if (!p.inhabitants.isEmpty()) {
						if (p.owner == null) {
							// Do the civ thing.
							Government g = pick(Government.values());
							Population starter = pick(p.inhabitants);
							l("The $sname on $pname achieve spaceflight and organise as a " + g.typeName + "!", starter.type, p);
							Civ c = new Civ(starter.type, p, g, d(3), historicalCivs);
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
