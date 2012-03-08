package com.zarkonnen.spacegen;

import java.util.ArrayList;

public class Science {
	static boolean advance(Civ actor, SpaceGen sg) {
		ArrayList<Planet> cands;
		switch (sg.d(9)) {
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
						SentientType st = SentientType.invent(sg, actor, p, null);
						p.inhabitants.add(new Population(st, 3));
						p.owner = actor;
						actor.colonies.add(p);
						sg.l("The $cname uplift the local " + st.getName() + " on $pname and incorporate the planet into their civilisation.", actor, p);
						return false;
					}
				}
			case 5:
				// ROBOTS!
				cands = new ArrayList<Planet>();
				lp: for (Planet p : actor.fullColonies()) {
					for (Population pop : p.inhabitants) {
						if (pop.type.base == SentientType.Base.ROBOTS) { continue lp; }
					}
					cands.add(p);
				}
				if (cands.isEmpty()) { return false; }
				Planet rp = sg.pick(cands);
				SentientType rob = SentientType.genRobots(sg, actor, rp, null);
				sg.l("The $cname create " + rob.getName() + " as servants on $pname.", actor, rp);
				rp.inhabitants.add(new Population(rob, 4));
				break;
			case 6:
				Planet target = actor.largestColony();
				if (target == null) { return false; }
				Agent probe = new Agent(AgentType.SPACE_PROBE, sg.year, sg.pick(new String[] {
					"Soj'r", "Monad", "Lun'hod", "Mar'er", "P'neer", "Dyad", "Triad"
				}));
				probe.target = target;
				probe.timer = 10 + sg.d(50);
				probe.originator = actor;
				sg.l("The $name launch a space probe called " + probe.name + " to explore the galaxy.", actor);
				sg.agents.add(probe);
				break;
			case 7:
			case 8:
				cands = new ArrayList<Planet>();
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
