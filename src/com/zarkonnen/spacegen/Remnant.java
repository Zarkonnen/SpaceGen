package com.zarkonnen.spacegen;

public class Remnant implements Stratum {
	Population remnant;
	int collapseTime;
	Cataclysm cat;

	public Remnant(Population remnant, int extinctionTime, Cataclysm cat) {
		this.remnant = remnant;
		this.collapseTime = extinctionTime;
		this.cat = cat;
	}
	
	@Override
	public String toString() {
		return "Remnants of a culture of " + remnant.type.name + " that collapsed" +
				(cat == null ? "" : " due to a " + cat.name) + " in " + collapseTime + ".";
	}

	@Override
	public int time() { return collapseTime; }
}
