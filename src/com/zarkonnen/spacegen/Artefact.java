package com.zarkonnen.spacegen;

public class Artefact {
	int created;
	Civ creator;
	ArtefactType type;
	String desc;

	public Artefact(int created, Civ creator, ArtefactType type, String desc) {
		this.created = created;
		this.creator = creator;
		this.type = type;
		this.desc = desc;
	}
	
	@Override
	public String toString() {
		return desc + " created by the " + creator.name + " in " + created;
	}
}
