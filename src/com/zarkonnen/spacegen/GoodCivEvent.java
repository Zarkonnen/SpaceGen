/**
Copyright 2012 David Stark

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.zarkonnen.spacegen;

import static com.zarkonnen.spacegen.Stage.*;
import static com.zarkonnen.spacegen.Main.*;

public enum GoodCivEvent {
	GOLDEN_AGE_OF_SCIENCE() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			rep.append("The ").append(actor.name).append(" enters a golden age of science! ");
			actor.setResources(actor.getResources() + 5);
			CivAction.BUILD_SCIENCE_OUTPOST.i(actor, sg, rep);
			actor.setScience(actor.getScience() + 10);
		}
	},
	GOLDEN_AGE_OF_INDUSTRY() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			rep.append("The ").append(actor.name).append(" enters a golden age of industry! ");
			actor.setResources(actor.getResources() + 10);
			CivAction.BUILD_MINING_BASE.i(actor, sg, rep);
			rep.append(" ");
			CivAction.BUILD_MINING_BASE.i(actor, sg, rep);
		}
	},
	GOLDEN_AGE_OF_ART() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			Planet col = sg.pick(actor.fullColonies());
			ArtefactType.Art artT = sg.pick(ArtefactType.Art.values());
			Artefact art = artT.create(actor, sg);
			rep.append("Artists on ").append(col.name).append(" create a ").append(art.desc).append(". ");
			col.addArtefact(art);
		}
	},
	POPULATION_BOOM() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			rep.append("The ").append(actor.name).append(" experiences a population boom! ");
			for (Planet col : actor.getColonies()) {
				for (Population p : col.inhabitants) { p.setSize(p.getSize() + 2); }
			}
		}
	},
	DEMOCRATISATION() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			if (actor.getGovt() == Government.REPUBLIC) {
				return;
			}
			String oldName = actor.name;
			for (Planet c : actor.getColonies()) { for (Population p : c.inhabitants) {
				if (!actor.fullMembers.contains(p.type)) {
					actor.fullMembers.add(p.type);
				}
			}}
			actor.setGovt(Government.REPUBLIC, sg.historicalCivNames);
			for (Planet p : actor.getColonies()) { for (Population pop : p.inhabitants) { pop.addUpdateImgs(); } }
			animate();
			rep.append("A popular movement overthrows the old guard of the ").append(oldName).append(" and declares the ").append(actor.name).append(".");
		}
	},
	SPAWN_ADVENTURER() {
		@Override public void i(Civ actor, SpaceGen sg, StringBuilder rep) {
			if (actor.getColonies().isEmpty()) { return; }
			SentientType st = sg.pick(actor.fullMembers);
			String name = "Captain " + sg.pick(st.base.nameStarts) + sg.pick(st.base.nameEnds);
			Planet p = sg.pick(actor.getColonies());
			Agent ag = new Agent(AgentType.ADVENTURER, sg.year, name, sg);
			ag.fleet = 2 + sg.d(6);
			ag.resources = sg.d(6);
			ag.originator = actor;
			ag.st = st;
			ag.setLocation(p);
			sg.agents.add(ag);
			rep.append(name).append(", space adventurer, blasts off from ").append(p.name).append(".");
		}
	},;
	// SPAWN_ADVENTURER
	// SPAWN_PRIVATEER
	
	public void i(Civ actor, SpaceGen sg, StringBuilder rep) { return; }
	public void invoke(Civ actor, SpaceGen sg) {
		StringBuilder rep = new StringBuilder();
		i(actor, sg, rep);
		if (rep.length() > 0) {
			sg.l(rep.toString());
			confirm();
		}
	}
}
