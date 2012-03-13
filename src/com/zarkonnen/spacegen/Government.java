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

import static com.zarkonnen.spacegen.CivAction.*;
import static com.zarkonnen.spacegen.SentientEncounterOutcome.*;


public enum Government {
	DICTATORSHIP(
			"Military Dictatorship",
			"Empire",
			2,
			new SentientEncounterOutcome[] {EXTERMINATE, EXTERMINATE, EXTERMINATE_FAIL, IGNORE, IGNORE, IGNORE, IGNORE, IGNORE, SUBJUGATE, SUBJUGATE, SUBJUGATE, SUBJUGATE, SUBJUGATE, SUBJUGATE},
			EXPLORE_PLANET, EXPLORE_PLANET, COLONISE_PLANET, BUILD_SCIENCE_OUTPOST, BUILD_MINING_BASE, BUILD_MILITARY_BASE, BUILD_MILITARY_BASE, BUILD_MILITARY_BASE, DO_RESEARCH, BUILD_WARSHIPS, BUILD_WARSHIPS, BUILD_WARSHIPS, BUILD_CONSTRUCTION),
	THEOCRACY(
			"Theocracy",
			"Church",
			4,
			new SentientEncounterOutcome[] {EXTERMINATE, EXTERMINATE, EXTERMINATE_FAIL, IGNORE, IGNORE, IGNORE, SUBJUGATE, SUBJUGATE, SUBJUGATE, SUBJUGATE, SUBJUGATE},
			EXPLORE_PLANET, COLONISE_PLANET, COLONISE_PLANET, COLONISE_PLANET, BUILD_MINING_BASE, BUILD_MILITARY_BASE, BUILD_MILITARY_BASE, BUILD_WARSHIPS, BUILD_CONSTRUCTION, BUILD_CONSTRUCTION),
	FEUDAL_STATE(
			"Feudal State",
			"Kingdom",
			2,
			new SentientEncounterOutcome[] {EXTERMINATE, EXTERMINATE_FAIL, IGNORE, IGNORE, IGNORE, IGNORE, IGNORE, SUBJUGATE, SUBJUGATE, SUBJUGATE, GIVE_FULL_MEMBERSHIP},
			EXPLORE_PLANET, COLONISE_PLANET, COLONISE_PLANET, BUILD_MINING_BASE, BUILD_MILITARY_BASE, DO_RESEARCH, BUILD_WARSHIPS, BUILD_WARSHIPS, BUILD_CONSTRUCTION),
	REPUBLIC(
			"Republic",
			"Republic",
			1,
			new SentientEncounterOutcome[] {EXTERMINATE, EXTERMINATE, EXTERMINATE_FAIL, IGNORE, IGNORE, IGNORE, IGNORE, IGNORE, SUBJUGATE, SUBJUGATE, GIVE_FULL_MEMBERSHIP, GIVE_FULL_MEMBERSHIP, GIVE_FULL_MEMBERSHIP, GIVE_FULL_MEMBERSHIP, GIVE_FULL_MEMBERSHIP, GIVE_FULL_MEMBERSHIP, GIVE_FULL_MEMBERSHIP},
			EXPLORE_PLANET, COLONISE_PLANET, BUILD_MINING_BASE, BUILD_MINING_BASE, BUILD_MILITARY_BASE, BUILD_SCIENCE_OUTPOST, DO_RESEARCH, BUILD_WARSHIPS, BUILD_CONSTRUCTION, BUILD_CONSTRUCTION);
	
	final String typeName;
	final String title;
	final int bombardP;
	final CivAction[] behaviour;
	SentientEncounterOutcome[] encounterOutcomes;

	private Government(String name, String title, int bombardP, SentientEncounterOutcome[] encounterOutcomes, CivAction... behaviour) {
		this.typeName = name;
		this.behaviour = behaviour;
		this.title = title;
		this.bombardP = bombardP;
		this.encounterOutcomes = encounterOutcomes;
	}
}
