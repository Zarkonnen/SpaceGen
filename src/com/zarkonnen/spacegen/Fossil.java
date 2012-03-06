package com.zarkonnen.spacegen;

public class Fossil implements Stratum {
	SpecialLifeform fossil;
	int fossilisationTime;
	Cataclysm cat;

	public Fossil(SpecialLifeform fossil, int fossilisationTime, Cataclysm cat) {
		this.fossil = fossil;
		this.fossilisationTime = fossilisationTime;
		this.cat = cat;
	}
	
	@Override
	public String toString() {
		return "Fossils of " + fossil.name.toLowerCase() + " that went extinct in " + fossilisationTime + 
				(cat == null ? "." : " due to a " + cat.name + ".");
	}

	@Override
	public int time() { return fossilisationTime; }
}
