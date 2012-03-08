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
		if (artefact.type == ArtefactType.PIRATE_TOMB || artefact.type == ArtefactType.ADVENTURER_TOMB) {
			return "The " + artefact + ", buried in " + lostTime + ".";
		}
		if (artefact.type == ArtefactType.WRECK) {
			return "The " + artefact + ".";
		}
		return "A " + artefact + ", " + status + " in " + lostTime + ".";
	}

	@Override
	public int time() { return lostTime; }
}
