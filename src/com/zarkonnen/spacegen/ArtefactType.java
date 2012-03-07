package com.zarkonnen.spacegen;

public interface ArtefactType {
	public static enum Art implements ArtefactType {
		STATUE,
		PAINTING,
		HOLOGRAM,
		FILM,
		HYMN;
		
		@Override
		public String getName() { return name().toLowerCase(); }

		String create(Civ actor, SpaceGen sg) {
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
			switch (sg.d(4)) {
				case 0:
					switch (actor.govt) {
						case DICTATORSHIP:
							return ret + " the Emperor of the " + actor.name;
						case FEUDAL_STATE:
							return ret + " the King of the " + actor.name;
						case REPUBLIC:
							return ret + " the President of the " + actor.name;
						case THEOCRACY:
							return ret + " the Autarch of the " + actor.name;
					}
				case 1:
					switch (actor.govt) {
						case DICTATORSHIP:
							return ret + " the Empress of the " + actor.name;
						case FEUDAL_STATE:
							return ret + " the King of the " + actor.name;
						case REPUBLIC:
							return ret + " the President of the " + actor.name;
						case THEOCRACY:
							return ret + " the Autarch of the " + actor.name;
					}
				case 2:
					return ret + " cheese";
				case 3:
					return ret + " " + sg.pick(actor.fullMembers).name;
				default:
					return ret + " space kittens";
			}
		}
	}
	
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
