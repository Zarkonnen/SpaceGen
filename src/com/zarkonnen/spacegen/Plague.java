package com.zarkonnen.spacegen;

import java.util.ArrayList;

public class Plague {
	String name;
	int lethality;
	int mutationRate;
	int transmissivity;
	int curability;
	ArrayList<SentientType> affects = new ArrayList<SentientType>();

	public Plague(SpaceGen sg) {
		name = sg.pick(Names.COLORS) + " " + sg.pick(new String[] { "Rot", "Death", "Plague", "Fever", "Wasting", "Pox"});
		lethality = sg.d(9);
		mutationRate = sg.d(3);
		transmissivity = sg.d(3);
		curability = sg.d(3);
	}

	Plague(Plague plague) {
		this.name = plague.name;
		this.lethality = plague.lethality;
		this.mutationRate = plague.mutationRate;
		this.transmissivity = plague.transmissivity;
		this.curability = plague.curability;
		affects.addAll(plague.affects);
	}
	
	public String desc() {
		String desc = name + ", which affects ";
		for (int i = 0; i < affects.size(); i++) {
			if (i > 0) {
				if (i == affects.size() - 1) {
					desc += " and ";
				} else {
					desc += ", ";
				}
			}
			desc += affects.get(i).getName();
		}
		return desc;
	}
}
