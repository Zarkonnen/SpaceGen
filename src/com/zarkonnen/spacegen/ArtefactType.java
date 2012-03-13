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

public interface ArtefactType {
	public static enum Art implements ArtefactType {
		STATUE,
		PAINTING,
		HOLOGRAM,
		FILM,
		HYMN;
		
		@Override
		public String getName() { return name().toLowerCase(); }

		public Artefact create(Civ actor, SpaceGen sg) {
			Artefact art = create2(actor, sg);
			art.setImg();
			return art;
		}
		
		public Artefact create2(Civ actor, SpaceGen sg) {
			Artefact art = new Artefact(sg.year, actor, this, "");
			String ret = "";
			switch (this) {
				case STATUE:
					ret = "statue of"; break;
				case PAINTING:
					ret = "painting of"; break;
				case HOLOGRAM:
					ret = "hologram of"; break;
				case FILM:
					ret = "film about"; break;
				case HYMN:
					ret = "hymn about"; break;
			}
			switch (sg.d(7)) {
				case 0:
				case 1:
					if (!sg.agents.isEmpty()) {
						Agent a = sg.pick(sg.agents);
						art.containedAgent = a;
						art.desc = ret + " " + a.name;
						return art;
					}
					// fallthru!
				case 2:
					art.containedST = sg.pick(actor.fullMembers);
					switch (actor.getGovt()) {
						case DICTATORSHIP:
							art.desc = ret + " the Emperor of the " + actor.name;
							return art;
						case FEUDAL_STATE:
							art.desc = ret + " the King of the " + actor.name;
							return art;
						case REPUBLIC:
							art.desc = ret + " the President of the " + actor.name;
							return art;
						case THEOCRACY:
							art.desc = ret + " the Autarch of the " + actor.name;
							return art;
					}
				case 3:
					art.containedST = sg.pick(actor.fullMembers);
					switch (actor.getGovt()) {
						case DICTATORSHIP:
							art.desc = ret + " the Empress of the " + actor.name;
							return art;
						case FEUDAL_STATE:
							art.desc = ret + " the Queen of the " + actor.name;
							return art;
						case REPUBLIC:
							art.desc = ret + " the President of the " + actor.name;
							return art;
						case THEOCRACY:
							art.desc = ret + " the Grand Matron of the " + actor.name;
							return art;
					}
				case 4:
				case 5:
					ArrayList<Artefact> arts = new ArrayList<Artefact>();
					for (Planet p : actor.colonies) {
						arts.addAll(p.artefacts);
					}
					if (!arts.isEmpty()) {
						art.containedArtefact = sg.pick(arts);
						art.desc = ret + " " + art.containedArtefact;
						return art;
					}
					// INTENTIONAL FALLTHROUGH!
				case 6:
					art.containedST = sg.pick(actor.fullMembers);
					art.desc = ret + " " + art.containedST.getName();
					return art;
				default:
					return create(actor, sg);
			}
		}
	}
	
	public static ArtefactType TIME_ICE = new ArtefactType() {
		@Override
		public String getName() { return "Block of Time Ice"; }
	};
	
	public static ArtefactType WRECK = new ArtefactType() {
		@Override
		public String getName() { return "Spaceship Wrech"; }
	};
	
	public static ArtefactType PIRATE_HOARD = new ArtefactType() {
		@Override
		public String getName() { return "Pirate Hoard"; }
	};
	
	public static ArtefactType PIRATE_TOMB = new ArtefactType() {
		@Override
		public String getName() { return "Pirate Tomb"; }
	};
	
	public static ArtefactType ADVENTURER_TOMB = new ArtefactType() {
		@Override
		public String getName() { return "Tomb"; }
	};
	
	public static enum Device implements ArtefactType {
		TELEPORT_GATE("Teleport Gate"),
		PLANET_DESTROYER("Planet Destroyer"),
		MIND_CONTROL_DEVICE("Mind Control Device"),
		MIND_READER("Mind Reader"),
		MASTER_COMPUTER("Master Computer"),
		YOUTH_SERUM("Youth Serum"),
		STASIS_CAPSULE("Stasis Capsule"),
		TIME_MACHINE("Time Machine"),
		LIVING_WEAPON("Living Weapon"),
		MIND_ARCHIVE("Mind Archive"),
		UNIVERSAL_NUTRIENT("Universal Nutrient"),
		VIRTUAL_REALITY_MATRIX("Virtual Reality Matrix"),
		UNIVERSAL_ANTIDOTE("Universal Antidote"),
		ARTIFICIAL_PLAGUE("Artificial Plague"),
		KILLER_MEME("Killer Meme"),
		UNIVERSAL_COMPUTER_VIRUS("Universal Computer Virus");
		
		final String name;

		private Device(String name) {
			this.name = name;
		}
		
		@Override
		public String getName() {
			return name;
		}

		String create(Civ actor, SpaceGen sg) {
			return name;
		}
	}
	
	public String getName();
}
