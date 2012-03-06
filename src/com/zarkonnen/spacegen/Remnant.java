package com.zarkonnen.spacegen;

public class Remnant implements Stratum {
	Population remnant;
	int collapseTime;
	Cataclysm cat;
	String reason;

	public Remnant(Population remnant, int extinctionTime, Cataclysm cat, String reason) {
		this.remnant = remnant;
		this.collapseTime = extinctionTime;
		this.cat = cat;
		this.reason = reason;
	}
	
	@Override
	public String toString() {
		return "Remnants of a culture of " + remnant.type.name + " that collapsed " +
				(cat == null ? reason : "due to a " + cat.name) + " in " + collapseTime + ".";
	}

	@Override
	public int time() { return collapseTime; }
}
