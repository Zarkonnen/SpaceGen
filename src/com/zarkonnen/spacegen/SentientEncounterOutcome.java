package com.zarkonnen.spacegen;

public enum SentientEncounterOutcome {
	SUBJUGATE("They subjugate the local $a."),
	GIVE_FULL_MEMBERSHIP("They incorporate the local $a into their civilization as equals."),
	IGNORE("They ignore the local $a."),
	EXTERMINATE("They mount a campaign of extermination against the local $a"),
	EXTERMINATE_FAIL("They attempt to exterminate the local $a");
	
	public final String desc;

	private SentientEncounterOutcome(String desc) {
		this.desc = desc;
	}
}
