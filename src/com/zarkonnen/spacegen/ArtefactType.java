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
					ret = ret + ""; break;
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
	
	public String getName();
}
