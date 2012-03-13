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
