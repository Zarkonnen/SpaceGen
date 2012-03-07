package com.zarkonnen.spacegen;

import java.util.ArrayList;

public class Science {
	static boolean advance(Civ actor, SpaceGen sg) {
		switch (sg.d(6)) {
			case 0:
				actor.techLevel++;
				if (actor.techLevel == 10) {
					sg.l("The highly advanced technology of the $name allows them to transcend the bounds of this universe. They vanish instantly.");
					for (Planet col : new ArrayList<Planet>(actor.colonies)) {
						col.transcend(sg.year);
					}
					sg.civs.remove(actor);
					return true;
				}
				break;
			case 1:
				// Send out space probe: todo.
				break;
			case 2:
				// Develop new weapons systems.
				sg.l("The $name develop powerful new weapons.", actor);
				actor.weapLevel++;
				break;
			case 3:
				for (Planet p : sg.planets) {
					if (!p.habitable && p.owner == null) {
						p.habitable = true;
						p.inhabitants.add(new Population(actor.fullMembers.get(0), 1));
						p.owner = actor;
						actor.colonies.add(p);
						sg.l("The $cname terraform and colonise $pname.", actor, p);
						return false;
					}
				}
				// INTENTIONAL FALLTHROUGH
			case 4:
				for (Planet p : sg.planets) {
					if (p.habitable && p.owner == null && p.inhabitants.isEmpty()) {
						SentientType st = sg.pick(SentientType.values());
						p.inhabitants.add(new Population(st, 3));
						p.owner = actor;
						actor.colonies.add(p);
						sg.l("The $cname uplift the local " + st.name + " on $pname and incorporate the planet into their civilisation.", actor, p);
						return false;
					}
				}
			case 5:
			case 6:
				ArrayList<Planet> cands = new ArrayList<Planet>();
				for (Planet p : actor.colonies) {
					if (p.has(StructureType.Standard.SCIENCE_LAB)) {
						cands.add(p);
					}
				}
				cands.add(actor.largestColony());
				Planet p = sg.pick(cands);
				ArtefactType.Device type = sg.pick(ArtefactType.Device.values());
				Artefact a = new Artefact(sg.year, actor, type, type.create(actor, sg));
				p.artefacts.add(a);
				sg.l("The $name develop a " + a.type.getName() + ".", actor);
		}
		
		return false;
	}
}
