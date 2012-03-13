package com.zarkonnen.spacegen;

import java.util.ArrayList;

import static com.zarkonnen.spacegen.Stage.*;
import static com.zarkonnen.spacegen.Main.*;

public class Science {
	static boolean advance(Civ actor, SpaceGen sg) {
		ArrayList<Planet> cands;
		switch (sg.d(9)) {
			case 0:
				actor.setTechLevel(actor.getTechLevel() + 1);
				if (actor.getTechLevel() == 10) {
					sg.l("The highly advanced technology of the $name allows them to transcend the bounds of this universe. They vanish instantly.");
					for (Planet col : new ArrayList<Planet>(actor.colonies)) {
						col.transcend(sg.year);
					}
					sg.civs.remove(actor);
					confirm();
					return true;
				}
				break;
			case 1:
				// Develop new weapons systems.
				sg.l("The $name develop powerful new weapons.", actor);
				actor.setWeapLevel(actor.getWeapLevel() + 1);
				confirm();
				break;
			case 2:
				Planet srcP = actor.largestColony();
				if (srcP.population() > 1) {
					for (Planet p : actor.reachables(sg)) {
						if (!p.habitable && p.getOwner() == null) {
							p.habitable = true;
							//p.inhabitants.add(new Population(actor.fullMembers.get(0), 1));

							Population srcPop = null;
							for (Population pop : srcP.inhabitants) {
								if (actor.fullMembers.contains(pop.type) && pop.getSize() > 1) {
									srcPop = pop;
								}
							}
							if (srcPop == null) { srcPop = sg.pick(srcP.inhabitants); }
							srcPop.send(p);

							p.setOwner(actor);
							actor.colonies.add(p);
							animate(tracking(p.sprite, change(p.sprite, Imager.get(p))));
							sg.l("The $cname terraform and colonise $pname.", actor, p);
							confirm();
							return false;
						}
					}
				}
				// INTENTIONAL FALLTHROUGH
			case 3:
				for (Planet p : actor.reachables(sg)) {
					if (p.habitable && p.getOwner() == null && p.inhabitants.isEmpty()) {
						SentientType st = SentientType.invent(sg, actor, p, null);
						new Population(st, 3, p);
						p.setOwner(actor);
						actor.colonies.add(p);
						sg.l("The $cname uplift the local " + st.getName() + " on $pname and incorporate the planet into their civilisation.", actor, p);
						confirm();
						return false;
					}
				}
			case 4:
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
				new Population(rob, 4, rp);
				confirm();
				break;
			case 5:
				Planet target = actor.largestColony();
				if (target == null) { return false; }
				Agent probe = new Agent(AgentType.SPACE_PROBE, sg.year, sg.pick(new String[] {
					"Soj'r", "Monad", "Lun'hod", "Mar'er", "P'neer", "Dyad", "Triad"
				}));
				probe.target = target;
				probe.timer = 8 + sg.d(25);
				probe.originator = actor;
				sg.l("The $name launch a space probe called " + probe.name + " to explore the galaxy.", actor);
				sg.agents.add(probe);
				confirm();
				break;
			case 6:
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
				p.addArtefact(a);
				sg.l("The $name develop a " + a.type.getName() + ".", actor);
				confirm();
		}
		
		return false;
	}
}
