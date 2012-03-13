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

import java.util.ArrayList;

import static com.zarkonnen.spacegen.Stage.*;
import static com.zarkonnen.spacegen.Main.*;

public class War {
	public static void doWar(Civ actor, SpaceGen sg) {
		if (actor.getMilitary() <= 0) { return; }
		ArrayList<Planet> targets = new ArrayList<Planet>();
		for (Planet p : sg.planets) {
			if (p.getOwner() != null && p.getOwner() != actor && actor.relation(p.getOwner()) == Diplomacy.Outcome.WAR) {
				targets.add(p);
			}
		}
		if (targets.isEmpty()) { return; }
		Planet target = sg.pick(targets);
		Civ victim = target.getOwner();
		if (actor.has(ArtefactType.Device.TIME_MACHINE)) {
			for (Planet p : new ArrayList<Planet>(victim.colonies)) {
				p.deCiv(sg.year / 2, null, "by a time vortex");
				p.setOwner(null);
			}
			sg.civs.remove(victim);
			Planet p = sg.pick(sg.planets);
			if (p.strata.isEmpty()) {
				p.strata.add(new LostArtefact("lost", sg.year / 4, actor.use(ArtefactType.Device.TIME_MACHINE)));
			} else {
				p.strata.add(0, new LostArtefact("lost", p.strata.get(0).time() / 2, actor.use(ArtefactType.Device.TIME_MACHINE)));
			}
			sg.l("The $name use their time machine to erase their hated enemies, the " + victim.name + ".", actor);
			confirm();
			return;
		}
		if (actor.has(ArtefactType.Device.KILLER_MEME)) {
			sg.l("The $name use their memetic weapon against the " + victim.name + ".", actor);
			BadCivEvent.MASS_HYSTERIA.invoke(target.getOwner(), sg);
			target.strata.add(new LostArtefact("forgotten", sg.year, actor.use(ArtefactType.Device.KILLER_MEME)));
			confirm();
			return;
		}
		if (actor.has(ArtefactType.Device.UNIVERSAL_COMPUTER_VIRUS)) {
			sg.l("The $name use their universal computer virus against the " + victim.name + ".", actor);
			BadCivEvent.MARKET_CRASH.invoke(target.getOwner(), sg);
			target.strata.add(new LostArtefact("forgotten", sg.year, actor.use(ArtefactType.Device.UNIVERSAL_COMPUTER_VIRUS)));
			confirm();
			return;
		}
		if (actor.has(ArtefactType.Device.ARTIFICIAL_PLAGUE)) {
			sg.l("The $name use their artificial plague against the " + victim.name + ".", actor);
			BadCivEvent.PLAGUE.invoke(target.getOwner(), sg);
			actor.use(ArtefactType.Device.ARTIFICIAL_PLAGUE);
			confirm();
			return;
		}
		
		Planet srcP = actor.largestColony();
		Sprite fleet = new Sprite(Imager.EXPEDITION, srcP.sprite.x - 48, srcP.sprite.y + 160 / 2 - 32 / 2);
		fleet.children.add(new CivSprite(actor, true));
		animate(add(fleet));
		animate(tracking(fleet, move(fleet, target.sprite.x - 48, target.sprite.y + 160 / 2 - 32 / 2)));
		animate(track(target.sprite), remove(fleet));
		Civ enemy = target.getOwner();
		
		int attack = actor.getMilitary() * (2 + (actor.getTechLevel() + 2 * actor.getWeapLevel()));
		int defence = target.population() + (target.has(StructureType.Standard.MILITARY_BASE) ? 5 * (target.getOwner().getTechLevel() + 2 * target.getOwner().getWeapLevel()) : 0);
		if (target.has(SentientType.Base.URSOIDS.specialStructure)) {
			defence += 4;
		}
		int attackRoll = sg.d(attack, 6);
		int defenceRoll = sg.d(defence, 6);
		if (attackRoll > defenceRoll) {
			actor.setMilitary(actor.getMilitary() - sg.d(actor.getMilitary() / 6 + 1));
			if (sg.d(6) < actor.getGovt().bombardP || target.getOwner().has(SentientType.Base.PARASITES)) {
				if (actor.has(ArtefactType.Device.PLANET_DESTROYER)) {
					target.deLive(sg.year, null, "when the planet was scoured by a superweapon of the " + actor.name);
					sg.l("The $cname attack $pname and use their planet destroyer to turn it into a lifeless cinder.", actor, target);
					confirm();
					return;
				}
				if (target.has(SentientType.Base.DEEP_DWELLERS.specialStructure)) {
					for (Structure st : new ArrayList<Structure>(target.structures)) {
						if (sg.p(3)) {
							target.strata.add(new Ruin(st, sg.year, null, "through orbital bombardment by the " + actor.name));
							target.removeStructure(st);
						}
					}
					sg.l("The $cname attack $pname, a colony of the " + enemy.name + ", and subject it to orbital bombardment. Its inhabitats hide in the dome built deep in the planet's crust and escape harm.", actor, target);
					confirm();
					return;
				}
				int deaths = 0;
				for (Population pop : new ArrayList<Population>(target.inhabitants)) {
					int pd = sg.d(pop.getSize()) + 1;
					if (pd >= pop.getSize()) {
						target.dePop(pop, sg.year, null, "due to orbital bombardment by the " + actor.name, null);
					} else {
						pop.setSize(pop.getSize() - pd);
					}
					deaths += pd;
				}
				if (target.population() == 0) {
					target.deCiv(sg.year, null, "due to orbital bombardment by the " + actor.name);
					sg.l("The $cname attack and raze $pname, a colony of the " + enemy.name + ".", actor, target);
				} else {
					for (Structure st : new ArrayList<Structure>(target.structures)) {
						if (sg.coin()) {
							target.strata.add(new Ruin(st, sg.year, null, "through orbital bombardment by the " + actor.name));
							target.removeStructure(st);
						}
					}
					sg.l("The $cname attack $pname, a colony of the " + enemy.name + ", and subject it to orbital bombardment, killing " + deaths + " billion.", actor, target);
				}
			} else {
				actor.setResources(actor.getResources() + enemy.getResources() / enemy.colonies.size() / 2);
				enemy.setResources(enemy.getResources() - enemy.getResources() / enemy.colonies.size());
				if (actor.has(ArtefactType.Device.MIND_CONTROL_DEVICE)) {
					sg.l("The $cname conquer $pname, a colony of the " + enemy.name + ", using their mind control device to gain control of the planet from orbit.", actor, target);
				} else {
					for (Structure st : new ArrayList<Structure>(target.structures)) {
						if (sg.p(4)) {
							target.strata.add(new Ruin(st, sg.year, null, "during the invasion of the " + actor.name));
							target.removeStructure(st);
						}
					}
					if (target.population() > 0) {
						int deaths = 0;
						for (Population pop : new ArrayList<Population>(target.inhabitants)) {
							int pd = sg.d(pop.getSize() - pop.getSize() / 2);
							if (pd >= target.population()) { pd = 1; }
							if (pd >= target.population()) { break; }
							if (pd >= pop.getSize()) {
								target.dePop(pop, sg.year, null, "during the invasion of the " + actor.name, null);
							} else {
								pop.setSize(pop.getSize() - pd);
							}
							deaths += pd;
						}
						if (deaths > 0) {
							sg.l("The $cname conquer $pname, a colony of the " + enemy.name + ", killing " + deaths + " billion in the process.", actor, target);
						} else {
							sg.l("The $cname conquer $pname, a colony of the " + enemy.name + ".", actor, target);
						}
					} else {
						sg.l("The $cname conquer $pname, a colony of the " + enemy.name + ".", actor, target);
					}
				}
				
				for (Artefact a : target.artefacts) {
					if (!(a.type instanceof ArtefactType.Device)) { continue; }
					sg.l("The $cname gain control of the " + a.type.getName() + " on $pname.", actor, target);
				}
				
				target.getOwner().colonies.remove(target);
				target.setOwner(actor);
				actor.colonies.add(target);
				
				for (Population pop : target.inhabitants) { pop.addUpdateImgs(); }
				animate();
			}
		} else {
			for (Structure st : new ArrayList<Structure>(target.structures)) {
				if (sg.p(6)) {
					target.strata.add(new Ruin(st, sg.year, null, "during an attack by the " + actor.name));
					target.removeStructure(st);
				}
			}
			actor.setMilitary(actor.getMilitary() - sg.d(actor.getMilitary() / 3 + 1));
			sg.l("The " + target.getOwner().name + " repel the " + actor.name + " at " + target.name + ".");
		}
		confirm();
	}
}
