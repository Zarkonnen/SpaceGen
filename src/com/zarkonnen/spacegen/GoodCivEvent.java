package com.zarkonnen.spacegen;

public enum GoodCivEvent {
	GOLDEN_AGE_OF_SCIENCE() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			rep.append("The ").append(actor.name).append(" enters a golden age of science! ");
			actor.resources += 5;
			CivAction.BUILD_SCIENCE_OUTPOST.i(actor, sg, rep);
			actor.science += 10;
		}
	},
	GOLDEN_AGE_OF_INDUSTRY() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			rep.append("The ").append(actor.name).append(" enters a golden age of industry! ");
			actor.resources += 10;
			CivAction.BUILD_MINING_BASE.i(actor, sg, rep);
			rep.append(" ");
			CivAction.BUILD_MINING_BASE.i(actor, sg, rep);
		}
	},
	GOLDEN_AGE_OF_ART() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			Planet col = sg.pick(actor.fullColonies());
			ArtefactType.Art art = sg.pick(ArtefactType.Art.values());
			String artDesc = art.create(actor, sg);
			rep.append("Artists on ").append(col.name).append(" create a ").append(artDesc).append(". ");
			col.artefacts.add(new Artefact(sg.year, actor, art, artDesc));
		}
	},
	POPULATION_BOOM() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			rep.append("The ").append(actor.name).append(" experiences a population boom! ");
			for (Planet col : actor.colonies) {
				for (Population p : col.inhabitants) { p.size++; }
			}
		}
	},
	DEMOCRATISATION() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			if (actor.govt == Government.REPUBLIC) {
				return;
			}
			String oldName = actor.name;
			for (Planet c : actor.colonies) { for (Population p : c.inhabitants) {
				if (!actor.fullMembers.contains(p.type)) {
					actor.fullMembers.add(p.type);
				}
			}}
			actor.govt = Government.REPUBLIC;
			actor.updateName(sg.historicalCivNames);
			rep.append("A popular movement overthrows the old guard of the ").append(oldName).append(" and declares the ").append(actor.name).append(".");
		}
	};
	// SPAWN_ADVENTURER
	// SPAWN_PRIVATEER
	
	public void i(Civ actor, SpaceGen sg, StringBuilder rep) { return; }
	public void invoke(Civ actor, SpaceGen sg) {
		StringBuilder rep = new StringBuilder();
		i(actor, sg, rep);
		if (rep.length() > 0) { sg.l(rep.toString()); }
	}
}
