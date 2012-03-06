package com.zarkonnen.spacegen;

public class Ruin implements Stratum {
	Structure structure;
	int ruinTime;
	Cataclysm cat;
	String reason;

	public Ruin(Structure structure, int ruinTime, Cataclysm cat, String reason) {
		this.structure = structure;
		this.ruinTime = ruinTime;
		this.cat = cat;
		this.reason = reason;
	}

	@Override
	public int time() { return ruinTime; }
	
	@Override
	public String toString() {
		return "The ruins of a " + structure + ", destroyed in " + ruinTime + " " +
 				(cat == null ? reason : "by a " + cat.name) + ".";
	}
}
