package com.zarkonnen.spacegen;

public class Ruin implements Stratum {
	Structure structure;
	int ruinTime;
	Cataclysm cat;

	public Ruin(Structure structure, int ruinTime, Cataclysm cat) {
		this.structure = structure;
		this.ruinTime = ruinTime;
		this.cat = cat;
	}

	@Override
	public int time() { return ruinTime; }
	
	@Override
	public String toString() {
		return "The ruins of a " + structure + ", destroyed in " + ruinTime + 
				(cat == null ? "." : " by a " + cat.name + ".");
	}
}
