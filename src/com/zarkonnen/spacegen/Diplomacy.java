package com.zarkonnen.spacegen;

import static com.zarkonnen.spacegen.Diplomacy.Outcome.*;

public class Diplomacy {
	public static enum Outcome {
		WAR,
		PEACE,
		UNION;
		
		public String desc(Civ encountered, Outcome previousStatus) {
			return desc2(previousStatus).replace("$b", encountered.name);
		}
		
		public String desc2(Outcome previousStatus) {
			switch (this) {
				case WAR:
					switch (previousStatus) {
						case WAR: return "Peace negotiations with the $b are unsuccessful and their war continues.";
						case PEACE: return "They declare war on the $b!";
						default: return "???";
					}
				case PEACE:
					switch (previousStatus) {
						case WAR: return "They sign a peace accord with the $b, ending their war.";
						case PEACE: return "They reaffirm their peaceful relations with the $b.";
						default: return "???";
					}
				case UNION:
					switch (previousStatus) {
						case WAR: return "In a historical moment, they put aside their differences with the $b, uniting the two empires.";
						case PEACE: return "In a historical moment, they agree to combine their civilization with the $b.";
						default: return "???";
					}
				default: return "???";
			}
		}
	}
	
	static Outcome[][][] TABLE = new Outcome[Government.values().length][Government.values().length][6];
	
	static {
		TABLE[Government.DICTATORSHIP.ordinal()][Government.DICTATORSHIP.ordinal()] = new Outcome[] {
			WAR, WAR, WAR, PEACE, PEACE, PEACE
		};
		TABLE[Government.DICTATORSHIP.ordinal()][Government.THEOCRACY.ordinal()] = new Outcome[] {
			WAR, WAR, WAR, WAR, PEACE, PEACE
		};
		TABLE[Government.DICTATORSHIP.ordinal()][Government.FEUDAL_STATE.ordinal()] = new Outcome[] {
			WAR, WAR, WAR, WAR, PEACE, PEACE
		};
		TABLE[Government.DICTATORSHIP.ordinal()][Government.REPUBLIC.ordinal()] = new Outcome[] {
			WAR, WAR, WAR, PEACE, PEACE, PEACE
		};
		
		TABLE[Government.THEOCRACY.ordinal()][Government.THEOCRACY.ordinal()] = new Outcome[] {
			WAR, WAR, WAR, WAR, WAR, PEACE
		};
		TABLE[Government.THEOCRACY.ordinal()][Government.FEUDAL_STATE.ordinal()] = new Outcome[] {
			WAR, WAR, WAR, PEACE, PEACE, PEACE
		};
		TABLE[Government.THEOCRACY.ordinal()][Government.REPUBLIC.ordinal()] = new Outcome[] {
			WAR, WAR, WAR, PEACE, PEACE, PEACE
		};
		
		TABLE[Government.FEUDAL_STATE.ordinal()][Government.FEUDAL_STATE.ordinal()] = new Outcome[] {
			WAR, WAR, WAR, PEACE, PEACE, UNION
		};
		TABLE[Government.FEUDAL_STATE.ordinal()][Government.REPUBLIC.ordinal()] = new Outcome[] {
			WAR, WAR, PEACE, PEACE, PEACE, PEACE
		};
		
		TABLE[Government.REPUBLIC.ordinal()][Government.REPUBLIC.ordinal()] = new Outcome[] {
			WAR, PEACE, PEACE, PEACE, PEACE, UNION
		};
	}
	
	public static Outcome meet(Civ a, Civ b, SpaceGen sg) {
		if (a.fullMembers.get(0).base == SentientType.Base.URSOIDS ||
			b.fullMembers.get(0).base == SentientType.Base.URSOIDS)
		{
			return WAR;
		}
		if (a.getGovt().ordinal() > b.getGovt().ordinal()) {
			Civ tmp = a;
			a = b;
			b = tmp;
		}
		
		Outcome o = sg.pick(TABLE[a.getGovt().ordinal()][b.getGovt().ordinal()]);
		if (o == Outcome.WAR && a.has(ArtefactType.Device.MIND_READER) && sg.coin()) {
			o = Outcome.PEACE;
		}
		if (o == Outcome.WAR && b.has(ArtefactType.Device.MIND_READER) && sg.coin()) {
			o = Outcome.PEACE;
		}
		return o;
	}
}
