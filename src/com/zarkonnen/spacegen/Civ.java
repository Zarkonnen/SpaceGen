package com.zarkonnen.spacegen;

import java.util.ArrayList;

public class Civ {
	ArrayList<SentientType> fullMembers = new ArrayList<SentientType>();
	Government govt;
	ArrayList<Planet> colonies = new ArrayList<Planet>();
	
	int resources = 0;
	int science = 0;
	String name;

	public Civ(SentientType st, Planet home, Government govt, int resources, ArrayList<Civ> historicals) {
		this.govt = govt;
		this.fullMembers.add(st);
		this.colonies.add(home);
		this.resources = resources;
		int nth = 0;
		lp: while (true) {
			nth++;
			String n = genName(nth);
			for (Civ c : historicals) {
				if (c.name.equals(n)) { continue lp; }
			}
			name = n;
			break;
		}
	}
		
	final String genName(int nth) {
		String n = "";
		if (nth > 1) { n = Names.nth(nth) + " "; }
		n += govt.title + " of ";
		for (int i = 0; i < fullMembers.size(); i++) {
			if (i > 0) {
				if (i == fullMembers.size() - 1) {
					n += " and ";
				} else {
					n += ", ";
				}
			}
			n += fullMembers.get(i).name;
		}
		return n;
	}
}
