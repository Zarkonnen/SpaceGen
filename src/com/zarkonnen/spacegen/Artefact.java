package com.zarkonnen.spacegen;

public class Artefact {
	int created;
	Civ creator;
	ArtefactType type;
	String desc;
	SentientType st;
	int creatorTechLevel;
	int specialValue;
	String creatorName;

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
	
	public Artefact(int created, String creatorName, ArtefactType type, String desc) {
		this.created = created;
		this.creatorName = creatorName;
		this.type = type;
		this.desc = desc;
	}
	
	@Override
	public String toString() {
		if (creatorName != null) { return desc + " created by " + creatorName + " in " + created; }
		if (creator == null) { return desc; }
		return desc + " created by the " + creator.name + " in " + created;
	}
}
