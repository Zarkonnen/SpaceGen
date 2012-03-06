package com.zarkonnen.spacegen;

import java.util.ArrayList;

public class Civ {
	ArrayList<SentientType> fullMembers = new ArrayList<SentientType>();
	Government govt;
	ArrayList<Planet> colonies = new ArrayList<Planet>();
	
	int resources = 0;
	int science = 0;

	public Civ(SentientType st, Planet home, Government govt, int resources) {
		this.govt = govt;
		this.fullMembers.add(st);
		this.colonies.add(home);
		this.resources = resources;
	}
	
	public String name() {
		String n = govt.name + " of ";
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
