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
			switch (sg.d(6)) {
				case 0:
				case 1:
					if (!sg.agents.isEmpty()) {
						Agent a = sg.pick(sg.agents);
						return ret + " " + a.name;
					}
					// fallthru!
				case 2:
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
				case 3:
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
				case 4:
					return ret + " cheese";
				case 5:
					return ret + " " + sg.pick(actor.fullMembers).getName();
				default:
					return ret + " space kittens";
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
