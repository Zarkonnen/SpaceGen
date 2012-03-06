package com.zarkonnen.spacegen;

public class LostArtefact implements Stratum {
	public String status;
	int lostTime;
	Artefact artefact;

	public LostArtefact(String status, int lostTime, Artefact artefact) {
		this.status = status;
		this.lostTime = lostTime;
		this.artefact = artefact;
	}
	
	@Override
	public String toString() {
		return "A " + artefact + ", " + status + " in " + lostTime + ".";
	}

	@Override
	public int time() { return lostTime; }
}
