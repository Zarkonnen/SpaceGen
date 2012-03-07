package com.zarkonnen.spacegen;

public class Artefact {
	int created;
	Civ creator;
	ArtefactType type;
	String desc;
	SentientType st;
	int creatorTechLevel;
	int specialValue;

	public Artefact(int created, Civ creator, ArtefactType type, String desc) {
		this.created = created;
		this.creator = creator;
		this.type = type;
		this.desc = desc;
		if (creator != null) {
			st = creator.fullMembers.get(0);
			creatorTechLevel = creator.techLevel;
		}
	}
	
	@Override
	public String toString() {
		if (creator == null) { return desc; }
		return desc + " created by the " + creator.name + " in " + created;
	}
}
