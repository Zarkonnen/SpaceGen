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

public class LostArtefact implements Stratum {
	public String status;
	int lostTime;
	Artefact artefact;

	public LostArtefact(String status, int lostTime, Artefact artefact) {
		this.status = status;
		this.lostTime = lostTime;
		this.artefact = artefact;
	}
	
	@Override
	public String toString() {
		if (artefact.type == ArtefactType.PIRATE_TOMB || artefact.type == ArtefactType.ADVENTURER_TOMB) {
			return "The " + artefact + ", buried in " + lostTime + ".";
		}
		if (artefact.type == ArtefactType.WRECK) {
			return "The " + artefact + ".";
		}
		return "A " + artefact + ", " + status + " in " + lostTime + ".";
	}

	@Override
	public int time() { return lostTime; }
}
